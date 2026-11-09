/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.models.base;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASCape
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10)
 */
public class ASCape extends ModelBiped {

    private final ModelRenderer bodyReplacement, lArm, rArm, headReplacement;

    public ModelRenderer cowl, mantle_l, mantle_r;
    public ModelRenderer bodyAnchor, body, plate;
    public ModelRenderer armLAnchor, armLpauldron, fitting_l;
    public ModelRenderer armRAnchor, armRpauldron, fitting_r;

    public ASCape() {
        float s = 0.01F;
        this.textureWidth = 64;
        this.textureHeight = 128;

        this.cowl = new ModelRenderer(this, 0, 33);
        this.cowl.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.cowl.addBox(-4.5F, -4.0F, -4.0F, 9, 5, 9, s);
        setRotateAngle(cowl, 0.2617993877991494F, 0.0F, 0.0F);
        this.mantle_l = new ModelRenderer(this, 0, 47);
        this.mantle_l.mirror = true;
        this.mantle_l.setRotationPoint(6.25F, 2.0F, 0.0F);
        this.mantle_l.addBox(-8.0F, -3.5F, 1.0F, 9, 21, 5, s);
        setRotateAngle(mantle_l, 0.08726646259971647F, 0.2617993877991494F, 0.0F);
        this.mantle_r = new ModelRenderer(this, 0, 47);
        this.mantle_r.setRotationPoint(-6.25F, 2.0F, 0.0F);
        this.mantle_r.addBox(-1.0F, -3.5F, 1.0F, 9, 21, 5, s);
        setRotateAngle(mantle_r, 0.08726646259971647F, -0.2617993877991494F, 0.0F);

        this.bodyAnchor = new ModelRenderer(this, 0, 41);
        this.bodyAnchor.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.bodyAnchor.addBox(-1.0F, 0.0F, -1.0F, 2, 2, 2, s);
        this.body = new ModelRenderer(this, 0, 0);
        this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.body.addBox(-4.5F, -0.5F, -3.0F, 9, 6, 6, s);
        this.plate = new ModelRenderer(this, 0, 12);
        this.plate.setRotationPoint(0.0F, 1.0F, -3.0F);
        this.plate.addBox(-3.5F, -0.5F, -1.0F, 7, 7, 2, s);
        setRotateAngle(plate, 0.08726646259971647F, 0.0F, 0.0F);

        this.armLAnchor = new ModelRenderer(this, 0, 41);
        this.armLAnchor.mirror = true;
        this.armLAnchor.setRotationPoint(4.0F, 2.0F, 0.0F);
        this.armLAnchor.addBox(0.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armLpauldron = new ModelRenderer(this, 0, 21);
        this.armLpauldron.mirror = true;
        this.armLpauldron.setRotationPoint(0.0F, 0.0F, -0.0F);
        this.armLpauldron.addBox(0.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_l = new ModelRenderer(this, 18, 12);
        this.fitting_l.setRotationPoint(0.5F, -3.0F, 0.0F);
        this.fitting_l.addBox(0.0F, -1.0F, -1.0F, 4, 1, 2, s);
        setRotateAngle(fitting_l, 0.0F, 0.0F, 0.08726646259971647F);

        this.armRAnchor = new ModelRenderer(this, 0, 41);
        this.armRAnchor.mirror = true;
        this.armRAnchor.setRotationPoint(-4.0F, 2.0F, 0.0F);
        this.armRAnchor.addBox(-2.0F, -1.0F, -1.0F, 2, 2, 2, s);
        this.armRpauldron = new ModelRenderer(this, 0, 21);
        this.armRpauldron.setRotationPoint(0.0F, 0.0F, 0.0F);
        this.armRpauldron.addBox(-5.55F, -3.0F, -3.0F, 5, 6, 6, s);
        this.fitting_r = new ModelRenderer(this, 18, 12);
        this.fitting_r.setRotationPoint(0.0F, -3.0F, 0.0F);
        this.fitting_r.addBox(-4.5F, -1.0F, -1.0F, 4, 1, 2, s);
        setRotateAngle(fitting_r, 0.0F, 0.0F, -0.08726646259971647F);

        // Build hierarchy so a single rendercall cascades down
        this.bodyReplacement = new ModelRenderer(this);
        this.bodyReplacement.addChild(this.bodyAnchor);
        this.bodyAnchor.addChild(this.body);
        this.body.addChild(this.plate);
        this.body.addChild(this.mantle_l);
        this.body.addChild(this.mantle_r);

        this.headReplacement = new ModelRenderer(this);
        this.headReplacement.addChild(this.cowl);

        this.lArm = new ModelRenderer(this);
        this.lArm.offsetX = -0.40F;
        this.lArm.addChild(this.armLAnchor);
        this.armLAnchor.addChild(this.armLpauldron);
        this.armLpauldron.addChild(this.fitting_l);

        this.rArm = new ModelRenderer(this);
        this.rArm.offsetX = 0.40F;
        this.rArm.addChild(this.armRAnchor);
        this.armRAnchor.addChild(this.armRpauldron);
        this.armRpauldron.addChild(this.fitting_r);

        this.bipedBody = this.bodyReplacement;
        this.bipedRightArm = this.rArm;
        this.bipedLeftArm = this.lArm;
        this.bipedHead = this.headReplacement;
    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw,
        float headPitch, float scale) {
        // RenderPlayer toggles visibility per armor slot; the mantle always shows cowl, body and arms.
        bipedBody.showModel = true;
        bipedRightArm.showModel = true;
        bipedLeftArm.showModel = true;
        bipedHead.showModel = true;
        bipedHeadwear.showModel = false;
        bipedRightLeg.showModel = false;
        bipedLeftLeg.showModel = false;
        super.render(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    private static void setRotateAngle(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
