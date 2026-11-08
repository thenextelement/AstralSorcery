package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import hellfirepvp.astralsorcery.common.event.listener.EventHandlerNetwork;

/** Replaces the 1.10 ASM patch PatchCatchCustomPayload (sky handler dimension list sent during login). */
@Mixin(NetHandlerPlayClient.class)
public abstract class MixinNetHandlerPlayClient {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void astralsorcery$catchPayload(S3FPacketCustomPayload packet, CallbackInfo ci) {
        if (EventHandlerNetwork.clientCatchWorldHandlerPayload(packet)) {
            ci.cancel();
        }
    }
}
