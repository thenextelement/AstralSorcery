package hellfirepvp.astralsorcery.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import hellfirepvp.astralsorcery.common.event.BlockModifyEvent;
import hellfirepvp.astralsorcery.common.util.BlockPos;

/**
 * Replaces the 1.10 ASM patch PatchBlockModify: fires {@link BlockModifyEvent} whenever a chunk actually
 * changes a block. Used by the starlight network, crafting table tracking and rock crystal buffers.
 */
@Mixin(Chunk.class)
public abstract class MixinChunk {

    @Shadow
    public abstract Block getBlock(int x, int y, int z);

    @Shadow
    public abstract int getBlockMetadata(int x, int y, int z);

    @Shadow
    @Final
    public int xPosition;

    @Shadow
    @Final
    public int zPosition;

    private Block astralsorcery$oldBlock;
    private int astralsorcery$oldMeta;

    @Inject(method = "func_150807_a", at = @At("HEAD"))
    private void astralsorcery$captureOld(int x, int y, int z, Block block, int meta,
        CallbackInfoReturnable<Boolean> cir) {
        astralsorcery$oldBlock = getBlock(x, y, z);
        astralsorcery$oldMeta = getBlockMetadata(x, y, z);
    }

    @Inject(method = "func_150807_a", at = @At("RETURN"))
    private void astralsorcery$fireModify(int x, int y, int z, Block block, int meta,
        CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue()) return;
        Block old = astralsorcery$oldBlock;
        astralsorcery$oldBlock = null;
        if (old == null) return;
        Chunk self = (Chunk) (Object) this;
        if (self.worldObj == null) return;
        BlockPos pos = new BlockPos((xPosition << 4) + x, y, (zPosition << 4) + z);
        MinecraftForge.EVENT_BUS.post(new BlockModifyEvent(self, pos, old, astralsorcery$oldMeta, block, meta));
    }
}
