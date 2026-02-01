package berty.plugincoder.GUI.guis.text;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TextHexColorGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;
    private String hexCode;
    private int selectedColorIndex=11;
    public TextHexColorGUI(PluginCoder plugin) {
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }
    private void createInventory() {
        gui=Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        gui.setItem(4,instructionItem);
        ItemStack negro=mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<7;i++){
            gui.setItem(10+i,negro);
            gui.setItem(28+i,negro);
            gui.setItem(37+i,negro);
        }
        ItemStack rojo=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/97d0b9b3c419d3e321397bedc6dcd649e51cc2fa36b883b02f4da39582cdff1b");
        gui.setItem(11,rojo);
        ItemStack verde=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/361e5b333c2a3868bb6a58b6674a2639323815738e77e053977419af3f77");
        gui.setItem(13,verde);
        ItemStack azul=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/c96540ce762125e398ca53d4cd9b668396d0467e128b30da5aa62be9ce060");
        gui.setItem(15,azul);
        ItemStack plus=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/60b55f74681c68283a1c1ce51f1c83b52e2971c91ee34efcb598df3990a7e7");
        meta=plus.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"+");
        plus.setItemMeta(meta);
        gui.setItem(33,plus);gui.setItem(42,plus);
        ItemStack minus=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/c3e4b533e4ba2dff7c0fa90f67e8bef36428b6cb06c45262631b0b25db85b");
        meta=minus.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"-");
        minus.setItemMeta(meta);
        gui.setItem(29,minus);gui.setItem(38,minus);
        updateInventoryLanguage();
    }

    public void updateInventoryLanguage() {
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
    }
    public void updateInventory(){
        selectedColorIndex=11;
        if(hexCode==null||!hexCode.matches("^#[A-Fa-f0-9]{6}$"))hexCode="#000000";
        updateHexCodeSign(hexCode);
        updateColorItemCode(hexCode,1);
        updateColorItemCode(hexCode,2);
        updateColorItemCode(hexCode,3);
        updateSelectedColor(11);
        hexCode=null;
    }
    private void updateColorItemCode(String hexCode,int colorChannelID){
        String colorCode=hexCode.replaceAll("^#([A-Fa-f0-9]{2})([A-Fa-f0-9]{2})([A-Fa-f0-9]{2})$","$"+colorChannelID);
        char[] hexCodeChars=new char[]{'#','0','0','0','0','0','0'};
        int lastColorIndex=colorChannelID*2;
        hexCodeChars[lastColorIndex-1]=colorCode.charAt(0);
        hexCodeChars[lastColorIndex]=colorCode.charAt(1);
        hexCode=new String(hexCodeChars);
        ItemStack colorItem=gui.getItem(colorChannelID*2+9);
        ItemMeta meta=colorItem.getItemMeta();
        meta.setDisplayName(mainPlugin.getCodeExecuter().parseHexColor(hexCode)+colorCode);
        colorItem.setItemMeta(meta);
    }
    public void updateSelectedColor(int clickedColorIndex){
        String hexCode=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName());
        int colorChannelID=clickedColorIndex-10;
        colorChannelID-=colorChannelID/2;
        String colorCode=hexCode.replaceAll("^#([A-Fa-f0-9]{2})([A-Fa-f0-9]{2})([A-Fa-f0-9]{2})$","$"+colorChannelID);
        ItemStack colorItem=gui.getItem(clickedColorIndex).clone();
        ItemMeta meta=colorItem.getItemMeta();
        int replacementIndex=colorChannelID*2;
        char[] itemHexColorCode=new char[]{'#','0','0','0','0','0','0'};
        itemHexColorCode[replacementIndex-1]=colorCode.charAt(0);
        meta.setDisplayName(mainPlugin.getCodeExecuter().parseHexColor(new String(itemHexColorCode))+colorCode.charAt(0));
        colorItem.setItemMeta(meta);
        gui.setItem(31,colorItem.clone());
        itemHexColorCode[replacementIndex-1]='0'; itemHexColorCode[replacementIndex]=colorCode.charAt(1);
        meta.setDisplayName(mainPlugin.getCodeExecuter().parseHexColor(new String(itemHexColorCode))+colorCode.charAt(1));
        colorItem.setItemMeta(meta);
        gui.setItem(40,colorItem);
        selectedColorIndex=clickedColorIndex;
    }
    public void updateColorCode(int clickedItemIndex){
        String hexCode=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName());
        int hexCharItemIndex=clickedItemIndex==29||clickedItemIndex==33?31:40;
        ItemStack hexCharItem=gui.getItem(hexCharItemIndex);
        String hexChar=ChatColor.stripColor(hexCharItem.getItemMeta().getDisplayName());
        int hexDigit = Integer.parseInt(hexChar, 16);
        hexDigit=(clickedItemIndex==33||clickedItemIndex==42?hexDigit+1:hexDigit-1)& 0xF;
        char newHexChar=Character.toUpperCase(Character.forDigit(hexDigit, 16));
        char[] hexCodeChars=hexCode.toCharArray();
        char[] itemHexColorCode=new char[]{'#','0','0','0','0','0','0'};
        int colorChannelID=selectedColorIndex-10;
        colorChannelID-=colorChannelID/2;
        int replacementIndex=colorChannelID*2;
        if(hexCharItemIndex==31)replacementIndex--;
        hexCodeChars[replacementIndex]=newHexChar;itemHexColorCode[replacementIndex]=newHexChar;
        hexCode=new String(hexCodeChars);
        ItemMeta meta=hexCharItem.getItemMeta();
        meta.setDisplayName(mainPlugin.getCodeExecuter().parseHexColor(new String(itemHexColorCode))+newHexChar);
        hexCharItem.setItemMeta(meta);
        updateColorItemCode(hexCode,colorChannelID);
        updateHexCodeSign(hexCode);
    }
    private void updateHexCodeSign(String hexCode){
        ItemStack guiItem=gui.getItem(4);
        ItemMeta meta=guiItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(hexCode));
        guiItem.setItemMeta(meta);
        gui.setItem(4,guiItem);
    }
    public void saveHexCode(){
        String hexCode=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName());
        PluginCoder.getCoderGUI().getTextGUI().saveToTextGUI(hexCode,false,false);
    }
    public void setHexCode(String hexCode) {
        this.hexCode = hexCode.toUpperCase();
    }

    public Inventory getGUI() {
        return gui;
    }
}
