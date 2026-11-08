package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets the vicio mantle glide without the server's "flying is not enabled" kick. */
@Mixin(NetHandlerPlayServer.class)
public interface AccessorNetHandlerPlayServer {

    @Accessor("floatingTickCount")
    void astralsorcery$setFloatingTickCount(int ticks);
}
