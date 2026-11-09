/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import hellfirepvp.astralsorcery.client.gui.journal.GuiJournalPages;
import hellfirepvp.astralsorcery.client.gui.journal.GuiProgressionRenderer;
import hellfirepvp.astralsorcery.client.gui.journal.GuiScreenJournal;
import hellfirepvp.astralsorcery.client.gui.journal.page.IGuiRenderablePage;
import hellfirepvp.astralsorcery.client.util.RenderingUtils;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GuiJournal
 * Created by HellFirePvP
 * Date: 11.08.2016 / 18:38
 */
public class GuiJournalProgression extends GuiScreenJournal {

    private static GuiJournalProgression currentInstance = null;
    public boolean expectReinit = false;
    public boolean rescaleAndRefresh = true;

    private static GuiProgressionRenderer progressionRenderer;

    private int bufX, bufY;
    private boolean dragging = false;

    private GuiJournalProgression() {
        super(0);
    }

    public static GuiJournalProgression getJournalInstance() {
        if (currentInstance != null) {
            return currentInstance;
        }
        return new GuiJournalProgression();
    }

    public static GuiScreenJournal getOpenJournalInstance() {
        GuiScreenJournal gui = GuiJournalPages.getClearOpenGuiInstance();
        if (gui == null) gui = getJournalInstance();
        return gui;
    }

    public static void resetJournal() {
        currentInstance = null;
        GuiJournalPages.getClearOpenGuiInstance();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        rescaleAndRefresh = false;
    }

    @Override
    public void initGui() {
        super.initGui();

        if (expectReinit) {
            expectReinit = false;
            return; // We ASSUME, that the state is valid.
        }

        if (currentInstance == null || progressionRenderer == null) {
            currentInstance = this;
            progressionRenderer = new GuiProgressionRenderer(currentInstance, guiHeight - 10, guiWidth - 10);
            progressionRenderer.centerMouse();
        }

        progressionRenderer.updateOffset(guiLeft + 10, guiTop + 10);
        progressionRenderer.setBox(10, 10, guiWidth - 10, guiHeight - 10);
        // progressionRenderer.resetOverlayText();

        if (rescaleAndRefresh) {
            progressionRenderer.resetZoom();
            progressionRenderer.unfocus();
            progressionRenderer.refreshSize();
            progressionRenderer.updateMouseState();
        } else {
            rescaleAndRefresh = true;
        }

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        int cleanedX = mouseX - guiLeft;
        int cleanedY = mouseY - guiTop;

        if (Mouse.isButtonDown(0) && progressionRenderer.realRenderBox.isInBox(cleanedX, cleanedY)) {
            if (dragging) {
                progressionRenderer.moveMouse(-(cleanedX - bufX), -(cleanedY - bufY));
            } else {
                bufX = cleanedX;
                bufY = cleanedY;
                dragging = true;
            }
        } else {
            progressionRenderer.applyMovedMouseOffset();
            dragging = false;
        }

        // Zooming stuff.
        int dWheelChange = Mouse.getDWheel();
        if (dWheelChange < 0) {
            progressionRenderer.handleZoomOut();
        }
        if (dWheelChange > 0) {
            progressionRenderer.handleZoomIn();
        }

        ScaledResolution res = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(
            (guiLeft + 27) * res.getScaleFactor(),
            (guiTop + 27) * res.getScaleFactor(),
            (guiWidth - 54) * res.getScaleFactor(),
            (guiHeight - 54) * res.getScaleFactor());
        progressionRenderer.drawProgressionPart(zLevel);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        drawDefault(textureResShell);

        Rectangle guiStar = null;
        if (!ResearchManager.clientProgress.wasOnceAttuned()) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            guiStar = IGuiRenderablePage.GUI_INTERFACE
                .drawInfoStar(guiLeft + guiWidth - 39, guiTop + 23, zLevel, 15, partialTicks);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        zLevel += 150;
        drawMouseHighlight(zLevel, getCurrentMousePoint(), guiStar);
        zLevel -= 150;

        GL11.glPopAttrib();
        GL11.glPopMatrix();
    }

    private void drawMouseHighlight(float zLevel, Point mousePoint, Rectangle starRect) {
        progressionRenderer.drawMouseHighlight(zLevel, mousePoint);

        if (starRect != null && starRect.contains(mousePoint)) {
            RenderingUtils.renderBlueTooltip(mousePoint.x, mousePoint.y, new LinkedList<String>() {

                {
                    add(I18n.format("misc.journal.info.1"));
                    add(
                        I18n.format(
                            "misc.journal.info.2",
                            GameSettings.getKeyDisplayString(mc.gameSettings.keyBindForward.getKeyCode()),
                            GameSettings.getKeyDisplayString(mc.gameSettings.keyBindBack.getKeyCode())));
                }
            }, IGuiRenderablePage.GUI_INTERFACE.getStandardFontRenderer());
        }
    }

    // Double-clicking a research cluster zooms into it (same steps as the scroll wheel, animated).
    private static final long DOUBLE_CLICK_MS = 350;
    private static final double CLUSTER_VIEW_SCALE = 6.4D;
    private long lastClickTime = 0;
    private Point lastClickPos = null;
    private int autoZoomTicks = 0;

    private void checkDoubleClick(Point p) {
        long now = Minecraft.getSystemTime();
        boolean dbl = lastClickPos != null && now - lastClickTime <= DOUBLE_CLICK_MS
            && Math.abs(lastClickPos.x - p.x) <= 4
            && Math.abs(lastClickPos.y - p.y) <= 4;
        lastClickTime = now;
        lastClickPos = p;
        if (dbl && progressionRenderer.getScalingFactor() < CLUSTER_VIEW_SCALE
            && progressionRenderer.getHoveredCluster() != null) {
            autoZoomTicks = 40;
            lastClickPos = null;
        }
    }

    private void tickAutoZoom() {
        if (autoZoomTicks <= 0) return;
        autoZoomTicks--;
        for (int i = 0; i < 2; i++) {
            if (progressionRenderer.getScalingFactor() >= CLUSTER_VIEW_SCALE) {
                autoZoomTicks = 0;
                return;
            }
            progressionRenderer.handleZoomIn();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();

        tickAutoZoom();

        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            progressionRenderer.handleZoomIn();
        } else if (Keyboard.isKeyDown(mc.gameSettings.keyBindBack.getKeyCode())) {
            progressionRenderer.handleZoomOut();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton != 0) return;
        Point p = new Point(mouseX, mouseY);
        if (rectConstellationBookmark != null && rectConstellationBookmark.contains(p)) {
            resetJournal();
            Minecraft.getMinecraft()
                .displayGuiScreen(GuiJournalConstellationCluster.getConstellationScreen());
            return;
        }
        if (rectPerkMapBookmark != null && rectPerkMapBookmark.contains(p)) {
            resetJournal();
            Minecraft.getMinecraft()
                .displayGuiScreen(new GuiJournalPerkMap());
            return;
        }
        checkDoubleClick(p);
        progressionRenderer.propagateClick(p);
    }

}
