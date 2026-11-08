package hellfirepvp.astralsorcery.mixins;

import org.jetbrains.annotations.NotNull;

import com.gtnewhorizon.gtnhmixins.builders.IMixins;
import com.gtnewhorizon.gtnhmixins.builders.MixinBuilder;

public enum Mixins implements IMixins {

    // spotless:off
    Vanilla(new MixinBuilder()
        .setPhase(Phase.EARLY)
        .addCommonMixins(
            "minecraft.MixinChunk",
            "minecraft.MixinEntityLivingBase",
            "minecraft.MixinItemStack",
            "minecraft.MixinWorld")
        .addClientMixins(
            "minecraft.MixinMinecraft",
            "minecraft.MixinNetHandlerPlayClient",
            "minecraft.MixinWorldClientSide")),

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
