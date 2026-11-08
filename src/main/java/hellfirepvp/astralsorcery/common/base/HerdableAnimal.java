/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: HerdableAnimal
 * Created by HellFirePvP
 * Date: 13.11.2016 / 16:43
 */
public interface HerdableAnimal<T extends EntityLivingBase> {

    static Map<Class<?>, HerdableAnimal> registryHerdable = new HashMap<>();

    public static <T extends EntityLivingBase> HerdableAnimal<T> getHerdable(T entity) {
        return registryHerdable.get(entity.getClass());
    }

    static HerdableAnimal getHerdable(Entity entity) {
        return registryHerdable.get(entity.getClass());
    }

    public static void init() {
        register(new Cow());
        register(new Chicken());
        register(new Sheep());
        register(new Pig());
        register(new Horse());
        // register(new Rabbit());
        register(new Squid());
        register(new Mooshroom());
        // register(new PolarBear());
    }

    /**
     * 1.10 rolled the entity's loot table; 1.7 has no loot tables, so the entity's own drop logic (dropFewItems) is run
     * with Forge's drop capturing and the captured items are returned.
     */
    static List<ItemStack> captureDrops(net.minecraft.entity.EntityLivingBase entity, float herdingLuck) {
        List<ItemStack> out = new java.util.ArrayList<>();
        try {
            if (DropHook.dropFewItems == null) {
                DropHook.dropFewItems = cpw.mods.fml.relauncher.ReflectionHelper.findMethod(
                    net.minecraft.entity.EntityLivingBase.class,
                    entity,
                    new String[] { "dropFewItems", "func_70628_a" },
                    boolean.class,
                    int.class);
            }
            entity.captureDrops = true;
            entity.capturedDrops.clear();
            DropHook.dropFewItems.invoke(entity, true, Math.max(0, (int) herdingLuck));
            for (net.minecraft.entity.item.EntityItem ei : entity.capturedDrops) {
                if (ei.getEntityItem() != null) out.add(ei.getEntityItem());
            }
        } catch (Exception ignored) {
            // leave drops empty if the entity misbehaves
        } finally {
            entity.captureDrops = false;
            entity.capturedDrops.clear();
        }
        return out;
    }

    class DropHook {

        static java.lang.reflect.Method dropFewItems = null;
    }

    public static void register(HerdableAnimal herd) {
        registryHerdable.put(herd.getEntityClass(), herd);
    }

    public Class<T> getEntityClass();

    @Nonnull
    public List<ItemStack> getHerdingDropsTick(T entity, World world, Random rand, float herdingLuck);

    // public static class PolarBear implements HerdableAnimal<EntityPolarBear> {
    //
    // @Override
    // public Class<EntityPolarBear> getEntityClass() {
    // return EntityPolarBear.class;
    // }
    //
    // @Nonnull
    // @Override
    // public List<ItemStack> getHerdingDropsTick(EntityPolarBear entity, World world, Random rand, float herdingLuck) {
    // LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
    // builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
    // return
    // world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_POLAR_BEAR).generateLootForPools(rand,
    // builder.build());
    // }
    // }

    public static class Mooshroom implements HerdableAnimal<EntityMooshroom> {

        @Override
        public Class<EntityMooshroom> getEntityClass() {
            return EntityMooshroom.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityMooshroom entity, World world, Random rand,
            float herdingLuck) {
            List<ItemStack> drops = captureDrops(entity, herdingLuck);
            if (rand.nextFloat() <= 0.05 * Math.max(0, herdingLuck)) {
                drops.add(new ItemStack(net.minecraft.init.Items.mushroom_stew));
            }
            return drops;
        }
    }

    // public static class Rabbit implements HerdableAnimal<EntityRabbit> {
    //
    // @Override
    // public Class<EntityRabbit> getEntityClass() {
    // return EntityRabbit.class;
    // }
    //
    // @Nonnull
    // @Override
    // public List<ItemStack> getHerdingDropsTick(EntityRabbit entity, World world, Random rand, float herdingLuck) {
    // LootContext.Builder builder = new LootContext.Builder((WorldServer) world);
    // builder.withDamageSource(CommonProxy.dmgSourceStellar).withLootedEntity(entity).withLuck(herdingLuck);
    // return
    // world.getLootTableManager().getLootTableFromLocation(LootTableList.ENTITIES_RABBIT).generateLootForPools(rand,
    // builder.build());
    // }
    // }

    public static class Horse implements HerdableAnimal<EntityHorse> {

        @Override
        public Class<EntityHorse> getEntityClass() {
            return EntityHorse.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityHorse entity, World world, Random rand, float herdingLuck) {
            return captureDrops(entity, herdingLuck);
        }
    }

    public static class Squid implements HerdableAnimal<EntitySquid> {

        @Override
        public Class<EntitySquid> getEntityClass() {
            return EntitySquid.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntitySquid entity, World world, Random rand, float herdingLuck) {
            return captureDrops(entity, herdingLuck);
        }

    }

    public static class Cow implements HerdableAnimal<EntityCow> {

        @Override
        public Class<EntityCow> getEntityClass() {
            return EntityCow.class;
        }

        @Override
        @Nonnull
        public List<ItemStack> getHerdingDropsTick(EntityCow entity, World world, Random rand, float herdingLuck) {
            List<ItemStack> drops = captureDrops(entity, herdingLuck);
            if (rand.nextFloat() <= 0.15 * Math.max(0, herdingLuck)) {
                drops.add(new ItemStack(net.minecraft.init.Items.milk_bucket));
            }
            return drops;
        }

    }

    public static class Chicken implements HerdableAnimal<EntityChicken> {

        @Override
        public Class<EntityChicken> getEntityClass() {
            return EntityChicken.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityChicken entity, World world, Random rand, float herdingLuck) {
            List<ItemStack> drops = captureDrops(entity, herdingLuck);
            if (rand.nextFloat() <= 0.2) {
                drops.add(new ItemStack(net.minecraft.init.Items.egg));
            }
            return drops;
        }

    }

    public static class Sheep implements HerdableAnimal<EntitySheep> {

        @Override
        public Class<EntitySheep> getEntityClass() {
            return EntitySheep.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntitySheep entity, World world, Random rand, float herdingLuck) {
            List<ItemStack> drops = captureDrops(entity, herdingLuck);
            if (rand.nextFloat() <= 0.01) {
                entity.setSheared(true);
            }
            if (!entity.getSheared() && rand.nextFloat() <= 0.05) {
                drops.add(new ItemStack(net.minecraft.init.Blocks.wool, rand.nextInt(2) + 1, entity.getFleeceColor()));
            }
            return drops;
        }

    }

    public static class Pig implements HerdableAnimal<EntityPig> {

        @Override
        public Class<EntityPig> getEntityClass() {
            return EntityPig.class;
        }

        @Nonnull
        @Override
        public List<ItemStack> getHerdingDropsTick(EntityPig entity, World world, Random rand, float herdingLuck) {
            return captureDrops(entity, herdingLuck);
        }

    }

}
