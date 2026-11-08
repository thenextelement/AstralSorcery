package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import hellfirepvp.astralsorcery.common.event.ClientKeyboardInputEvent;

/**
 * Replaces the 1.10 ASM patch PatchKeyboardEvent: lets AS swallow keyboard input (e.g. while the camera is
 * controlled by a celestial gateway / observatory transformer).
 */
@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;next()Z", remap = false))
    private boolean astralsorcery$keyboardNext() {
        if (!Keyboard.next()) return false;
        if (MinecraftForge.EVENT_BUS.post(new ClientKeyboardInputEvent())) {
            while (Keyboard.next()) {
                // drain
            }
            return false;
        }
        return true;
    }
}
