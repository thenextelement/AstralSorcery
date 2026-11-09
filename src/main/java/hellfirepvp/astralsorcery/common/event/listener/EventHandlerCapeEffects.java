/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectAevitas;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectArmara;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectBootes;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectDiscidia;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectEvorsio;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectFornax;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectHorologium;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectLucerna;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectMineralis;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.VicioGlideHandler;
import hellfirepvp.astralsorcery.common.entities.EntitySpectralTool;
import hellfirepvp.astralsorcery.common.event.EntityKnockbackEvent;
import hellfirepvp.astralsorcery.common.item.wearable.ItemCape;
import hellfirepvp.astralsorcery.common.lib.Constellations;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktParticleEvent;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.CropHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCapeEffects
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10; vicio's elytra flight lives in VicioGlideHandler)
 */
public class EventHandlerCapeEffects implements ITickHandler {

    private static final Random rand = new Random();

    public static EventHandlerCapeEffects INSTANCE = new EventHandlerCapeEffects();

    private EventHandlerCapeEffects() {}

    // Prevent event overflow
    private static boolean discidiaChainingAttack = false;
    private static boolean evorsioChainingBreak = false;

    private static boolean isRealPlayer(EntityPlayer pl) {
        return pl instanceof EntityPlayerMP && !MiscUtils.isPlayerFakeMP((EntityPlayerMP) pl);
    }

    @SubscribeEvent
    public void breakBlock(BlockEvent.BreakEvent event) {
        if (event.world.isRemote || evorsioChainingBreak) return;
        EntityPlayer pl = event.getPlayer();
        if (!isRealPlayer(pl)) return;
        Block block = event.block;
        int meta = event.blockMetadata;
        ItemStack held = pl.getHeldItem();
        World world = event.world;

        CapeEffectPelotrio pel = ItemCape.getCapeEffect(pl, Constellations.pelotrio);
        if (pel != null && held != null) {
            BlockPos above = new BlockPos(pl).add(0, 1, 0);
            boolean pickBlock = "pickaxe".equalsIgnoreCase(block.getHarvestTool(meta)) || (!block.getMaterial()
                .isToolNotRequired() && Items.diamond_pickaxe.canHarvestBlock(block, held));
            if (pickBlock && held.getItem()
                .getToolClasses(held)
                .contains("pickaxe")) {
                if (rand.nextFloat() < pel.getChanceSpawnPick()) {
                    world.spawnEntityInWorld(
                        new EntitySpectralTool(
                            world,
                            above,
                            new ItemStack(Items.diamond_pickaxe),
                            EntitySpectralTool.ToolTask.createPickaxeTask()));
                    return;
                }
            }
            if ((block.isWood(world, event.x, event.y, event.z) || block.isLeaves(world, event.x, event.y, event.z))
                && held.getItem()
                    .getToolClasses(held)
                    .contains("axe")) {
                if (rand.nextFloat() < pel.getChanceSpawnAxe()) {
                    world.spawnEntityInWorld(
                        new EntitySpectralTool(
                            world,
                            above,
                            new ItemStack(Items.diamond_axe),
                            EntitySpectralTool.ToolTask.createLogTask()));
                }
            }
        }

        CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
        if (ev != null && held != null
            && !held.getItem()
                .getToolClasses(held)
                .isEmpty()) {
            evorsioChainingBreak = true;
            try {
                MovingObjectPosition mop = rayTraceLook(pl, 6);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    ev.breakBlocksPlane(
                        (EntityPlayerMP) pl,
                        mop.sideHit,
                        world,
                        new BlockPos(event.x, event.y, event.z));
                }
            } finally {
                evorsioChainingBreak = false;
            }
        }
    }

    private static MovingObjectPosition rayTraceLook(EntityPlayer pl, double reach) {
        Vec3 from = Vec3.createVectorHelper(pl.posX, pl.posY + pl.getEyeHeight(), pl.posZ);
        if (pl.worldObj.isRemote) from.yCoord = pl.posY; // client player posY already is eye height
        Vec3 look = pl.getLook(1F);
        Vec3 to = from.addVector(look.xCoord * reach, look.yCoord * reach, look.zCoord * reach);
        return pl.worldObj.func_147447_a(from, to, false, false, true);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onHurt(LivingHurtEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        if (!(event.entityLiving instanceof EntityPlayer)) return;
        EntityPlayer pl = (EntityPlayer) event.entityLiving;

        CapeEffectDiscidia cd = ItemCape.getCapeEffect(pl, Constellations.discidia);
        if (cd != null) {
            cd.writeLastAttackDamage(event.ammount);
        }
        CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
        if (ca != null && ca.shouldPreventDamage(event.source, false)) {
            event.setCanceled(true);
            return;
        }
        CapeEffectBootes bo = ItemCape.getCapeEffect(pl, Constellations.bootes);
        if (bo != null && event.source.getEntity() instanceof EntityLivingBase) {
            bo.onPlayerDamagedByEntity(pl, (EntityLivingBase) event.source.getEntity());
        }
        if (event.source.isFireDamage()) {
            CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
            if (cf != null) {
                cf.healFor(pl, event.ammount);
                float mul = cf.getDamageMultiplier();
                if (mul <= 0) {
                    event.setCanceled(true);
                    return;
                }
                event.ammount *= mul;
            }
        } else {
            CapeEffectHorologium horo = ItemCape.getCapeEffect(pl, Constellations.horologium);
            if (horo != null) {
                horo.onHurt(pl);
            }
        }
    }

    @SubscribeEvent
    public void onKill(LivingDeathEvent event) {
        if (event.entityLiving.worldObj.isRemote) return;
        DamageSource ds = event.source;
        if (ds.getEntity() instanceof EntityPlayer) {
            EntityPlayer pl = (EntityPlayer) ds.getEntity();
            if (!isRealPlayer(pl)) return;
            CapeEffectEvorsio ev = ItemCape.getCapeEffect(pl, Constellations.evorsio);
            if (ev != null) {
                ev.deathAreaDamage(ds, event.entityLiving);
            }
        }
    }

    @SubscribeEvent
    public void onAttack(LivingAttackEvent event) {
        if (discidiaChainingAttack) return;
        if (event.entityLiving.worldObj.isRemote) return;
        DamageSource ds = event.source;
        if (!(ds.getEntity() instanceof EntityPlayer)) return;
        EntityPlayer attacker = (EntityPlayer) ds.getEntity();
        if (!isRealPlayer(attacker)) return;

        CapeEffectDiscidia cd = ItemCape.getCapeEffect(attacker, Constellations.discidia);
        if (cd != null) {
            double added = cd.getLastAttackDamage();
            if (added > 0) {
                discidiaChainingAttack = true;
                try {
                    // the stored damage goes to the attacked entity
                    EntityLivingBase target = event.entityLiving;
                    int pre = target.hurtResistantTime;
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(DamageSource.causePlayerDamage(attacker), (float) added);
                    target.hurtResistantTime = 0;
                    target.attackEntityFrom(CommonProxy.dmgSourceStellar, (float) (added / 2));
                    target.hurtResistantTime = Math.max(pre, target.hurtResistantTime);
                } finally {
                    discidiaChainingAttack = false;
                }
            }
        }
        CapeEffectPelotrio pel = ItemCape.getCapeEffect(attacker, Constellations.pelotrio);
        if (pel != null && attacker.getHeldItem() != null && rand.nextFloat() < pel.getChanceSpawnSword()) {
            attacker.worldObj.spawnEntityInWorld(
                new EntitySpectralTool(
                    attacker.worldObj,
                    new BlockPos(attacker).add(0, 1, 0),
                    new ItemStack(Items.diamond_sword),
                    EntitySpectralTool.ToolTask.createAttackTask()));
        }
    }

    /** Vanilla divides the dig speed by 5 under water; the octans mantle undoes that. */
    @SubscribeEvent
    public void onWaterBreak(PlayerEvent.BreakSpeed event) {
        EntityPlayer pl = event.entityPlayer;
        if (pl.isInsideOfMaterial(Material.water) && !EnchantmentHelper.getAquaAffinityModifier(pl)) {
            if (ItemCape.getCapeEffect(pl, Constellations.octans) != null) {
                event.newSpeed = event.originalSpeed * 5;
            }
        }
    }

    /** 1.12 gives the octans mantle knockback resistance in water through an attribute; 1.7 armor has no attributes. */
    @SubscribeEvent
    public void onKnockback(EntityKnockbackEvent event) {
        if (event.entityLiving instanceof EntityPlayer && event.entityLiving.isInWater()
            && ItemCape.getCapeEffect((EntityPlayer) event.entityLiving, Constellations.octans) != null) {
            event.setCanceled(true);
        }
    }

    private void tickAevitasEffect(EntityPlayer pl) {
        CapeEffectAevitas cd = ItemCape.getCapeEffect(pl, Constellations.aevitas);
        if (cd == null) return;
        float range = cd.getRange();
        World w = pl.worldObj;
        if (rand.nextFloat() < cd.getPotency()) {
            AxisAlignedBB bb = AxisAlignedBB.getBoundingBox(
                pl.posX - range,
                pl.posY - range,
                pl.posZ - range,
                pl.posX + range,
                pl.posY + range,
                pl.posZ + range);
            List<?> players = w.getEntitiesWithinAABB(EntityPlayer.class, bb);
            for (Object o : players) {
                EntityPlayer player = (EntityPlayer) o;
                if (player.isDead) continue;
                player.heal(0.3F);
                player.getFoodStats()
                    .addStats(2, 0.4F);
            }
        }
        if (rand.nextFloat() < cd.getTurnChance()) {
            int x = MathHelper.floor_double(pl.posX) + Math.round(-range + 1 + (2 * range * rand.nextFloat()));
            int y = MathHelper.floor_double(pl.posY) + Math.round(-range + 1 + (2 * range * rand.nextFloat()));
            int z = MathHelper.floor_double(pl.posZ) + Math.round(-range + 1 + (2 * range * rand.nextFloat()));
            BlockPos at = new BlockPos(x, y, z);
            Block b = w.getBlock(x, y, z);
            boolean changed = false;
            if (b instanceof BlockFlower && (b == Blocks.red_flower || b == Blocks.yellow_flower)) {
                // 1.12 "Plants": turn a flower into a random other flower
                if (rand.nextInt(9) == 0) {
                    w.setBlock(x, y, z, Blocks.yellow_flower, 0, 3);
                } else {
                    w.setBlock(x, y, z, Blocks.red_flower, rand.nextInt(9), 3);
                }
                changed = true;
            } else {
                CropHelper.GrowablePlant growable = CropHelper.wrapPlant(w, at);
                if (growable != null) {
                    growable.tryGrow(w, rand);
                    changed = true;
                }
            }
            if (changed) {
                PktParticleEvent ev = new PktParticleEvent(
                    PktParticleEvent.ParticleEventType.CE_CROP_INTERACT,
                    x,
                    y,
                    z);
                PacketChannel.CHANNEL.sendToAllAround(ev, PacketChannel.pointFromPos(w, at, 16));
            }
        }
    }

    private void tickOctansEffect(EntityPlayer pl) {
        CapeEffectOctans ceo = ItemCape.getCapeEffect(pl, Constellations.octans);
        if (ceo != null && pl.isInsideOfMaterial(Material.water)) {
            if (pl.getAir() < 300) {
                pl.setAir(300);
            }
            ceo.onWaterHealTick(pl);
        }
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (type != TickEvent.Type.PLAYER) return;
        EntityPlayer pl = (EntityPlayer) context[0];
        Side side = (Side) context[1];
        VicioGlideHandler.tick(pl);
        if (ItemCape.getWornCape(pl) == null) return;
        if (side == Side.SERVER) {
            if (!isRealPlayer(pl)) return;
            tickAevitasEffect(pl);
            CapeEffectFornax cf = ItemCape.getCapeEffect(pl, Constellations.fornax);
            if (cf != null && pl.isBurning()) {
                cf.attemptMelt(pl);
            }
            CapeEffectArmara ca = ItemCape.getCapeEffect(pl, Constellations.armara);
            if (ca != null) {
                ca.wornTick();
            }
            tickOctansEffect(pl);
            CapeEffectBootes bo = ItemCape.getCapeEffect(pl, Constellations.bootes);
            if (bo != null) {
                bo.onPlayerTick(pl);
            }
        } else {
            clientTick(pl);
        }
    }

    @cpw.mods.fml.relauncher.SideOnly(Side.CLIENT)
    private void clientTick(EntityPlayer pl) {
        CapeArmorEffect cae = ItemCape.getCapeEffect(pl);
        if (cae != null) {
            cae.playActiveParticleTick(pl);
        }
        CapeEffectLucerna luc = ItemCape.getCapeEffect(pl, Constellations.lucerna);
        if (luc != null && pl == net.minecraft.client.Minecraft.getMinecraft().thePlayer) {
            luc.playClientHighlightTick(pl);
        }
        CapeEffectMineralis min = ItemCape.getCapeEffect(pl, Constellations.mineralis);
        if (min != null && pl == net.minecraft.client.Minecraft.getMinecraft().thePlayer) {
            min.playClientHighlightTick(pl);
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.PLAYER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Cape-EventHandler";
    }
}
