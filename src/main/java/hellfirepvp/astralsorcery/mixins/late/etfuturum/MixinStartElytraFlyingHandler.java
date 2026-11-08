package hellfirepvp.astralsorcery.mixins.late.etfuturum;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import ganymedes01.etfuturum.configuration.configs.ConfigMixins;
import ganymedes01.etfuturum.elytra.IElytraPlayer;
import ganymedes01.etfuturum.network.StartElytraFlyingHandler;
import ganymedes01.etfuturum.network.StartElytraFlyingMessage;
import hellfirepvp.astralsorcery.common.integrations.efr.ElytraIntegration;

/** The server-side takeoff check wants an actual ItemArmorElytra; accept the vicio mantle as well. */
@Mixin(value = StartElytraFlyingHandler.class, remap = false)
public abstract class MixinStartElytraFlyingHandler {

    @Inject(
        method = "onMessage(Lganymedes01/etfuturum/network/StartElytraFlyingMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;",
        at = @At("HEAD"),
        cancellable = true)
    private void astralsorcery$vicioTakeoff(StartElytraFlyingMessage message, MessageContext ctx,
        CallbackInfoReturnable<IMessage> cir) {
        EntityPlayer player = ((NetHandlerPlayServer) ctx.netHandler).playerEntity;
        if (player == null || ElytraIntegration.getVicioMantle(player) == null) return;
        IElytraPlayer ep = (IElytraPlayer) player;
        if (!player.onGround && (ConfigMixins.enableNewElytraTakeoffLogic || player.motionY < 0.0D)
            && !ep.etfu$isElytraFlying()
            && !player.isInWater()) {
            ep.etfu$setElytraFlying(true);
            cir.setReturnValue(null);
        }
    }
}
