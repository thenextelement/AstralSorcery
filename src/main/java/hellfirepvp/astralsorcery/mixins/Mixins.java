package hellfirepvp.astralsorcery.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    Vanilla(new MixinBuilder()
        .setPhase(Phase.EARLY)
        .addCommonMixins(
            "minecraft.AccessorNetHandlerPlayServer",
            "minecraft.MixinChunk",
            "minecraft.MixinEntityLivingBase",
            "minecraft.MixinItemStack",
            "minecraft.MixinWorld")
        .addClientMixins(
            "minecraft.MixinMinecraft",
            "minecraft.MixinNetHandlerPlayClient",
            "minecraft.MixinWorldClientSide")),

    // Et Futurum Requiem: the vicio mantle works as an elytra (optional)
    EtFuturumElytra(new MixinBuilder()
        .setPhase(Phase.LATE)
        .addRequiredMod(TargetedMod.ETFUTURUM)
        .addCommonMixins(
            "etfuturum.MixinItemArmorElytra",
            "etfuturum.MixinStartElytraFlyingHandler")
        .addClientMixins(
            "etfuturum.MixinLayerBetterElytra")),

    ;
    // spotless:on

    private final MixinBuilder builder;

    Mixins(MixinBuilder builder) {
        this.builder = builder;
    }

    @NotNull
    @Override
    public MixinBuilder getBuilder() {
        return builder;
    }
}
