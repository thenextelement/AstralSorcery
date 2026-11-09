/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.entities;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.effect.EffectHelper;
import hellfirepvp.astralsorcery.client.effect.EntityComplexFX;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFacingParticle;
import hellfirepvp.astralsorcery.common.CommonProxy;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.impl.CapeEffectPelotrio;
import hellfirepvp.astralsorcery.common.util.BlockPos;
import hellfirepvp.astralsorcery.common.util.MiscUtils;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EntitySpectralTool
 * Created by HellFirePvP
 * (backported from 1.12.2-1.8.5 to 1.7.10: 1.7 has no flying move helper, so the tool steers itself
 * through blocks towards its target instead of using an AI task)
 */
public class EntitySpectralTool extends Entity {

    private static final int DW_ITEM = 10;

    private ToolTask taskType = ToolTask.BREAK_BLOCK;
    private int ticksUntilDeath = 0;

    private BlockPos breakTarget = null;
    private EntityLivingBase attackTarget = null;
    private int actionTicks = 0;

    public EntitySpectralTool(World worldIn) {
        super(worldIn);
        setSize(0.6F, 0.8F);
        this.noClip = true;
    }

    public EntitySpectralTool(World world, BlockPos spawnPos, ItemStack tool, ToolTask task) {
        this(world);
        setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
        setItem(tool);
        this.taskType = task;
        this.ticksUntilDeath = 100 + rand.nextInt(40);
    }

    @Override
    protected void entityInit() {
        this.dataWatcher.addObjectByDataType(DW_ITEM, 5);
    }

    private void setItem(ItemStack tool) {
        this.dataWatcher.updateObject(DW_ITEM, tool);
        this.dataWatcher.setObjectWatched(DW_ITEM);
    }

    @Nullable
    public ItemStack getItem() {
        return this.dataWatcher.getWatchableObjectItemStack(DW_ITEM);
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (worldObj.isRemote) {
            spawnAmbientEffects();
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            return;
        }
        this.ticksUntilDeath--;
        if (this.ticksUntilDeath <= 0) {
            setDead();
            return;
        }
        tickTask();
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.8;
        this.motionY *= 0.8;
        this.motionZ *= 0.8;
    }

    private void steerTowards(double x, double y, double z, double speed) {
        double dx = x - posX, dy = y - posY, dz = z - posZ;
        double len = MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
        if (len < 1E-3) return;
        double s = Math.min(speed * 0.1, len);
        this.motionX += dx / len * s;
        this.motionY += dy / len * s;
        this.motionZ += dz / len * s;
        this.rotationYaw = (float) (Math.atan2(dz, dx) * 180D / Math.PI) - 90F;
    }

    private void tickTask() {
        boolean resetTimer = false;
        switch (taskType) {
            case BREAK_BLOCK:
            case BREAK_LOG:
                if (breakTarget != null
                    && worldObj.isAirBlock(breakTarget.getX(), breakTarget.getY(), breakTarget.getZ())) {
                    breakTarget = null;
                    resetTimer = true;
                }
                if (breakTarget == null) {
                    if (ticksExisted % 5 == 0) breakTarget = findBreakTarget(taskType == ToolTask.BREAK_LOG);
                    break;
                }
                double bx = breakTarget.getX() + 0.5, by = breakTarget.getY() + 0.5, bz = breakTarget.getZ() + 0.5;
                steerTowards(bx, by, bz, 1.5);
                if (getDistanceSq(bx, by, bz) < 3D) {
                    actionTicks++;
                    int needed = taskType == ToolTask.BREAK_LOG ? CapeEffectPelotrio.getTicksBreakBlockAxe()
                        : CapeEffectPelotrio.getTicksBreakBlockPick();
                    if (actionTicks > needed && worldObj instanceof WorldServer) {
                        MiscUtils.breakBlockWithoutPlayer((WorldServer) worldObj, breakTarget);
                        breakTarget = null;
                        resetTimer = true;
                    }
                }
                break;
            case ATTACK_MONSTER:
                if (attackTarget != null && (attackTarget.isDead || attackTarget.getHealth() <= 0)) {
                    attackTarget = null;
                    resetTimer = true;
                }
                if (attackTarget == null) {
                    attackTarget = findClosestMonster();
                    break;
                }
                double ax = attackTarget.posX, ay = attackTarget.boundingBox.minY + attackTarget.height / 2,
                    az = attackTarget.posZ;
                steerTowards(ax, ay, az, 1.7);
                if (getDistanceSq(ax, ay, az) < 3D) {
                    actionTicks++;
                    if (actionTicks > CapeEffectPelotrio.getTicksSwordAttacks()) {
                        attackTarget
                            .attackEntityFrom(CommonProxy.dmgSourceStellar, CapeEffectPelotrio.getSwordAttackDamage());
                        resetTimer = true;
                    }
                }
                break;
        }
        if (resetTimer) {
            actionTicks = 0;
        }
    }

    @Nullable
    private BlockPos findBreakTarget(boolean logs) {
        int range = logs ? 10 : 8;
        int cx = MathHelper.floor_double(posX), cy = MathHelper.floor_double(posY), cz = MathHelper.floor_double(posZ);
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (int xx = -range; xx <= range; xx++) {
            for (int yy = -range; yy <= range; yy++) {
                for (int zz = -range; zz <= range; zz++) {
                    int x = cx + xx, y = cy + yy, z = cz + zz;
                    double d = xx * xx + yy * yy + zz * zz;
                    if (d >= bestDist) continue;
                    if (!isBreakable(x, y, z, logs)) continue;
                    best = new BlockPos(x, y, z);
                    bestDist = d;
                }
            }
        }
        return best;
    }

    private boolean isBreakable(int x, int y, int z, boolean logs) {
        Block b = worldObj.getBlock(x, y, z);
        if (b.isAir(worldObj, x, y, z) || worldObj.getTileEntity(x, y, z) != null) return false;
        float hardness = b.getBlockHardness(worldObj, x, y, z);
        if (hardness < 0 || hardness > 10) return false;
        int meta = worldObj.getBlockMetadata(x, y, z);
        if (logs) {
            if (!b.isWood(worldObj, x, y, z) && !b.isLeaves(worldObj, x, y, z)) return false;
            return b.getMaterial()
                .isToolNotRequired() || Items.diamond_axe.canHarvestBlock(b, new ItemStack(Items.diamond_axe));
        }
        String tool = b.getHarvestTool(meta);
        if (tool != null) {
            return "pickaxe".equals(tool) && b.getHarvestLevel(meta) <= 3;
        }
        return !b.getMaterial()
            .isToolNotRequired() && Items.diamond_pickaxe.canHarvestBlock(b, new ItemStack(Items.diamond_pickaxe));
    }

    @Nullable
    private EntityLivingBase findClosestMonster() {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(posX - 8, posY - 8, posZ - 8, posX + 8, posY + 8, posZ + 8);
        List<?> list = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
        EntityLivingBase closest = null;
        double dist = Double.MAX_VALUE;
        for (Object o : list) {
            EntityLivingBase e = (EntityLivingBase) o;
            if (e.isDead || !e.isCreatureType(EnumCreatureType.monster, false)) continue;
            double d = e.getDistanceSqToEntity(this);
            if (d < dist) {
                dist = d;
                closest = e;
            }
        }
        return closest;
    }

    @SideOnly(Side.CLIENT)
    private void spawnAmbientEffects() {
        if (rand.nextFloat() < 0.2F) {
            Color c = IConstellation.weak;
            double x = posX + rand.nextFloat() * width - (width / 2);
            double y = posY + rand.nextFloat() * (height / 2) + 0.2;
            double z = posZ + rand.nextFloat() * width - (width / 2);
            EntityFXFacingParticle p = EffectHelper.genericFlareParticle(x, y, z);
            p.setColor(c)
                .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
            p.scale(rand.nextFloat() * 0.5F + 0.3F);
            p.setMaxAge(30 + rand.nextInt(20));
            if (rand.nextFloat() < 0.8F) {
                p = EffectHelper.genericFlareParticle(x, y, z);
                p.setColor(Color.WHITE)
                    .enableAlphaFade(EntityComplexFX.AlphaFunction.FADE_OUT);
                p.scale(rand.nextFloat() * 0.2F + 0.1F);
                p.setMaxAge(20 + rand.nextInt(10));
            }
        }
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        if (compound.hasKey("AS_SpectralItem")) {
            setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("AS_SpectralItem")));
        }
        int task = compound.getInteger("AS_ToolTask");
        this.taskType = ToolTask.values()[MathHelper.clamp_int(task, 0, ToolTask.values().length - 1)];
        this.ticksUntilDeath = compound.getInteger("AS_ToolDeathTicks");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        ItemStack item = getItem();
        if (item != null) {
            compound.setTag("AS_SpectralItem", item.writeToNBT(new NBTTagCompound()));
        }
        compound.setInteger("AS_ToolTask", this.taskType.ordinal());
        compound.setInteger("AS_ToolDeathTicks", this.ticksUntilDeath);
    }

    public static enum ToolTask {

        BREAK_BLOCK,
        BREAK_LOG,
        ATTACK_MONSTER;

        public static ToolTask createPickaxeTask() {
            return BREAK_BLOCK;
        }

        public static ToolTask createLogTask() {
            return BREAK_LOG;
        }

        public static ToolTask createAttackTask() {
            return ATTACK_MONSTER;
        }
    }
}
