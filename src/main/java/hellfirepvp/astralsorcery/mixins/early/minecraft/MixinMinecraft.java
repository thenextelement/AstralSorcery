/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hellfirepvp.astralsorcery.common.event.ClientKeyboardInputEvent;

/**
 * Replaces the 1.10 ASM patch PatchKeyboardEvent: lets AS swallow keyboard input (e.g. while the camera is flown by a
 * celestial gateway). Unlike a redirect of the Keyboard.next() loop this leaves vanilla's input handling untouched
 * (and doesn't fight lwjgl3ify's input emulation): only when a listener cancels the event are the pending keyboard
 * events drained before vanilla reads them.
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Inject(method = "runTick", at = @At("HEAD"))
    private void astralsorcery$swallowKeyboard(CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new ClientKeyboardInputEvent())) {
            while (Keyboard.next()) {
                // drain
            }
            KeyBinding.unPressAllKeys();
        }
    }
}
