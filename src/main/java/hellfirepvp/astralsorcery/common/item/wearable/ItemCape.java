/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.wearable;

import java.awt.Color;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import hellfirepvp.astralsorcery.client.models.base.ASCape;
import hellfirepvp.astralsorcery.common.constellation.ConstellationRegistry;
import hellfirepvp.astralsorcery.common.constellation.IConstellation;
import hellfirepvp.astralsorcery.common.constellation.IMinorConstellation;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeArmorEffect;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectFactory;
import hellfirepvp.astralsorcery.common.constellation.cape.CapeEffectRegistry;
import hellfirepvp.astralsorcery.common.item.ItemDynamicColor;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemCape
 * Created by HellFirePvP
 * Date: 09.10.2017 / 23:08
 * (backported from 1.12.2-1.8.5 to 1.7.10: Mantle of the Stars)
 */
public class ItemCape extends ItemArmor implements ItemDynamicColor {

    /** ItemArmor armor type index of the chest slot. */
    private static final int CHEST = 1;
    /** EntityPlayer#inventory.armorInventory index of the chest slot. */
    private static final int ARMOR_INV_CHEST = 2;

    @SideOnly(Side.CLIENT)
    private Object objASCape;
    @SideOnly(Side.CLIENT)
    private IIcon overlay;

    public ItemCape() {
        super(RegistryItems.imbuedLeatherMaterial, 0, CHEST);
        setUnlocalizedName("ItemCape");
        setCreativeTab(RegistryItems.creativeTabAstralSorcery);
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (IConstellation c : ConstellationRegistry.getAllConstellations()) {
            if (c instanceof IMinorConstellation) continue;
            if (CapeEffectRegistry.getArmorEffect(c) == null) continue;
            ItemStack stack = new ItemStack(item);
            setAttunedConstellation(stack, c);
            list.add(stack);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        IConstellation cst = getAttunedConstellation(stack);
        if (cst != null) {
            tooltip.add(EnumChatFormatting.BLUE + StatCollector.translateToLocal(cst.getUnlocalizedName()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, int armorSlot) {
        if (objASCape == null) {
            objASCape = new ASCape();
        }
        return (ModelBiped) objASCape;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "astralsorcery:textures/models/as_cape.png";
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister reg) {
        this.itemIcon = reg.registerIcon("astralsorcery:mantle");
        this.overlay = reg.registerIcon("astralsorcery:mantle_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? overlay : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        return getColorForItemStack(stack, pass) & 0xFFFFFF;
    }

    @Override
    public int getColorForItemStack(ItemStack stack, int tintIndex) {
        if (tintIndex != 1) return 0xFFFFFF;
        IConstellation cst = getAttunedConstellation(stack);
        if (cst != null) {
            Color c = cst.getConstellationColor();
            return 0xFF000000 | c.getRGB();
        }
        return 0xFF000000;
    }

    // ItemArmor's leather dye handling: the mantle is not dyeable
    @Override
    public boolean hasColor(ItemStack stack) {
        return false;
    }

    @Override
    public int getColor(ItemStack stack) {
        return 0xFFFFFF;
    }

    @Nullable
    public static ItemStack getWornCape(@Nullable EntityPlayer entity) {
        if (entity == null) return null;
        ItemStack stack = entity.inventory.armorInventory[ARMOR_INV_CHEST];
        return stack != null && stack.getItem() instanceof ItemCape ? stack : null;
    }

    @Nullable
    public static CapeArmorEffect getCapeEffect(@Nullable EntityPlayer entity) {
        ItemStack stack = getWornCape(entity);
        if (stack == null || getAttunedConstellation(stack) == null) return null;
        return getCapeEffect(stack);
    }

    @Nullable
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nullable EntityPlayer entity,
        @Nonnull IConstellation expectedConstellation) {
        ItemStack stack = getWornCape(entity);
        if (stack == null) return null;
        IConstellation cst = getAttunedConstellation(stack);
        if (cst == null || cst != expectedConstellation) {
            return null;
        }
        return getCapeEffect(stack);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <V extends CapeArmorEffect> V getCapeEffect(@Nonnull ItemStack stack) {
        IConstellation cst = getAttunedConstellation(stack);
        if (cst == null) {
            return null;
        }
        CapeEffectFactory<? extends CapeArmorEffect> call = CapeEffectRegistry.getArmorEffect(cst);
        if (call == null) {
            return null;
        }
        try {
            NBTTagCompound cmp = NBTHelper.getPersistentData(stack);
            return (V) call.deserializeCapeEffect(cmp);
        } catch (Exception exc) {
            return null;
        }
    }

    @Nullable
    public static IConstellation getAttunedConstellation(@Nullable ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ItemCape)) {
            return null;
        }
        return IConstellation.readFromNBT(NBTHelper.getPersistentData(stack));
    }

    public static void setAttunedConstellation(@Nullable ItemStack stack, @Nonnull IConstellation cst) {
        if (stack == null || !(stack.getItem() instanceof ItemCape)) {
            return;
        }
        cst.writeToNBT(NBTHelper.getPersistentData(stack));
    }

}
