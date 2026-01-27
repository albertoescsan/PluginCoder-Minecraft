package berty.plugincoder.interpreter.classes.minigame.game.team;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

public class TeamEquipment {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;
    private Map<Integer,ItemStack> contents=new HashMap<>();

    public TeamEquipment(Color color){
        helmet=getColoredArmor(Material.LEATHER_HELMET,color);
        chestplate=getColoredArmor(Material.LEATHER_CHESTPLATE,color);
        leggings=getColoredArmor(Material.LEATHER_LEGGINGS,color);
        boots=getColoredArmor(Material.LEATHER_BOOTS,color);
    }
    private static ItemStack getColoredArmor(Material armorType,Color color) {
        ItemStack armor = new ItemStack(armorType);
        LeatherArmorMeta meta = (LeatherArmorMeta) armor.getItemMeta();
        meta.setColor(color);
        armor.setItemMeta(meta);
        return armor;
    }
    public void equip(Player player){
        player.getInventory().clear();
        player.getInventory().setHelmet(helmet);
        player.getInventory().setChestplate(chestplate);
        player.getInventory().setLeggings(leggings);
        player.getInventory().setBoots(boots);
        for(int slot:contents.keySet())player.getInventory().setItem(slot,contents.get(slot));
    }
    public ItemStack getHelmet() {
        return helmet;
    }

    public void setHelmet(ItemStack helmet) {
        this.helmet = helmet;
    }

    public ItemStack getChestplate() {
        return chestplate;
    }

    public void setChestplate(ItemStack chestplate) {
        this.chestplate = chestplate;
    }

    public ItemStack getLeggings() {
        return leggings;
    }

    public void setLeggings(ItemStack leggings) {
        this.leggings = leggings;
    }

    public ItemStack getBoots() {
        return boots;
    }

    public void setBoots(ItemStack boots) {
        this.boots = boots;
    }

    public Map<Integer, ItemStack> getContents() {
        return contents;
    }
}
