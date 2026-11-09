/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import net.minecraft.entity.player.EntityPlayerMP;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.VicioGlideHandler;
import io.netty.buffer.ByteBuf;

/**
 * Client -> server: the vicio mantle started/stopped gliding (1.7 stand-in for 1.12's PktElytraCapeState).
 */
public class PktVicioGlide implements IMessage, IMessageHandler<PktVicioGlide, IMessage> {

    private boolean gliding;

    public PktVicioGlide() {}

    public PktVicioGlide(boolean gliding) {
        this.gliding = gliding;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.gliding = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.gliding);
    }

    @Override
    public IMessage onMessage(PktVicioGlide message, MessageContext ctx) {
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player != null) {
            VicioGlideHandler.setGliding(player, message.gliding && VicioGlideHandler.canGlide(player));
        }
        return null;
    }
}
