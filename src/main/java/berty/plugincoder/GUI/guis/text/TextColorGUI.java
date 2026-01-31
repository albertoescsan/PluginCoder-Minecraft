package berty.plugincoder.GUI.guis.text;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;


public class TextColorGUI {

    private PluginCoder mainPlugin;
    private Inventory gui;

    public TextColorGUI(PluginCoder plugin) {
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui=Bukkit.createInventory(null,45," ");
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage() {
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack rojoOscuro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/97d0b9b3c419d3e321397bedc6dcd649e51cc2fa36b883b02f4da39582cdff1b");
        ItemMeta meta=rojoOscuro.getItemMeta();
        String[] colorTexts=PluginCoder.getCoderGUI().getGuiText("colorNames").split(",");
        meta.setDisplayName(ChatColor.DARK_RED+colorTexts[0]);
        rojoOscuro.setItemMeta(meta);
        gui.setItem(10,rojoOscuro);
        ItemStack rojo=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/4bac77520b9eee65068ef1cd8abeadb013b4de3953fd29ac68e90e4866227");
        meta=rojo.getItemMeta();
        meta.setDisplayName(ChatColor.RED+colorTexts[1]);
        rojo.setItemMeta(meta);
        gui.setItem(11,rojo);
        ItemStack naranja=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/fea590b681589fb9b0e8664ee945b41eb3851faf66aaf48525fba169c34270");
        meta=naranja.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+colorTexts[2]);
        naranja.setItemMeta(meta);
        gui.setItem(12,naranja);
        ItemStack amarillo=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/14c4141c1edf3f7e41236bd658c5bc7b5aa7abf7e2a852b647258818acd70d8");
        meta=amarillo.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+colorTexts[3]);
        amarillo.setItemMeta(meta);
        gui.setItem(13,amarillo);
        ItemStack verde=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/361e5b333c2a3868bb6a58b6674a2639323815738e77e053977419af3f77");
        meta=verde.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+colorTexts[4]);
        verde.setItemMeta(meta);
        gui.setItem(14,verde);
        ItemStack verdeOscuro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/36f69f7b7538b41dc3439f3658abbd59facca366f190bcf1d6d0a026c8f96");
        meta=verdeOscuro.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN+colorTexts[5]);
        verdeOscuro.setItemMeta(meta);
        gui.setItem(15,verdeOscuro);
        ItemStack aqua=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/f9e16979309b5a9b673d60d1390bbab0d0385eac7254d828ada2a36a46f73a59");
        meta=aqua.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+colorTexts[6]);
        aqua.setItemMeta(meta);
        gui.setItem(16,aqua);
        ItemStack aquaOscuro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/975b7ac9f0c712303cd3b654e646ce1c4bf243ab348a6a25370f2603e79a62a0");
        meta=aquaOscuro.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA+colorTexts[7]);
        aquaOscuro.setItemMeta(meta);
        gui.setItem(19,aquaOscuro);
        ItemStack azul=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/6dd97e9919bbb434dce8565c98d3740df151096e16fb8557bd95fb96d273fc2");
        meta=azul.getItemMeta();
        meta.setDisplayName(ChatColor.BLUE+colorTexts[8]);
        azul.setItemMeta(meta);
        gui.setItem(20,azul);
        ItemStack azulOscuro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/c96540ce762125e398ca53d4cd9b668396d0467e128b30da5aa62be9ce060");
        meta=azulOscuro.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_BLUE+colorTexts[9]);
        azulOscuro.setItemMeta(meta);
        gui.setItem(21,azulOscuro);
        ItemStack magenta=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9133fa52dd74d711e53747da963b8adecf92db946be113b56c38b3dc270eeb3");
        meta=magenta.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE+colorTexts[10]);
        magenta.setItemMeta(meta);
        gui.setItem(22,magenta);
        ItemStack morado=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/7c19a516746ffe6342f42811582cf86ffe287311ff03b5cf1aaca9cd542a8");
        meta=morado.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_PURPLE+colorTexts[11]);
        morado.setItemMeta(meta);
        gui.setItem(23,morado);
        ItemStack blanco=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/e5a770e7e44b3a1e6c3b83a97ff6997b1f5b26550e9d7aa5d5021a0c2b6ee");
        meta=blanco.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+colorTexts[12]);
        blanco.setItemMeta(meta);
        gui.setItem(24,blanco);
        ItemStack gris=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/31c45a59550143a44ed4e87ce2955e4a13e94cdfd4c64dee881dfb48dd92e");
        meta=gris.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+colorTexts[13]);
        gris.setItemMeta(meta);
        gui.setItem(25,gris);
        ItemStack grisOscuro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/f2f085c6b3cb228e5ba81df562c4786762f3c257127e9725c77b7fd301d37");
        meta=grisOscuro.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY+colorTexts[14]);
        grisOscuro.setItemMeta(meta);
        gui.setItem(28,grisOscuro);
        ItemStack negro=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9ddebbb062f6a385a91ca05f18f5c0acbe33e2d06ee9e7416cef6ee43dfe2fb");
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.BLACK+colorTexts[15]);
        negro.setItemMeta(meta);
        gui.setItem(29,negro);
        ItemStack bold=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/242bed9ecfdbdba9d0064e3936168c0ce684cc346610d2097d42944ebf81ecc9");
        meta=bold.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+""+ChatColor.BOLD+colorTexts[16]);
        bold.setItemMeta(meta);
        gui.setItem(30,bold);
        ItemStack italic=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/bf3b8158e4c3d717c0f1061bc5a4ba34986bb1851c021e5b5070c62d312e2254");
        meta=italic.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+""+ChatColor.ITALIC+colorTexts[17]);
        italic.setItemMeta(meta);
        gui.setItem(31,italic);
        ItemStack underline=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/7f328c3afe7f3e8c1bae8699e3dcace0bb63b43145941217551cfd6e65853f86");
        meta=underline.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+""+ChatColor.UNDERLINE+colorTexts[18]);
        underline.setItemMeta(meta);
        gui.setItem(32,underline);
        ItemStack strike=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/4122111ed2c1ac03799e4463ce5a86908372a219292129f43c36f5e77ccf0c5b");
        meta=strike.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+""+ChatColor.STRIKETHROUGH+colorTexts[19]);
        strike.setItemMeta(meta);
        gui.setItem(33,strike);
        ItemStack reset=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/be0fd10199e8e4fcdabcae4f85c85918127a7c5553ad235f01c56d18bb9470d3");
        meta=reset.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+colorTexts[20]);
        reset.setItemMeta(meta);
        gui.setItem(34,reset);
        if(mainPlugin.getVersionNumber()>=16){
            //ItemStack hexColor=PluginCoder.getCoderGUI().getPlayerHead("");
            ItemStack hexColor=new ItemStack(Material.PLAYER_HEAD);
            meta=hexColor.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"Hexadecimal colors");
            hexColor.setItemMeta(meta);
            gui.setItem(40,hexColor);
        }

    }
    public ItemStack getColorItem(String color){
        List<String> colores=Arrays.asList("DARK_RED","RED","ORANGE","YELLOW","GREEN","DARK_GREEN",
                "AQUA","CYAN","BLUE","DARK_BLUE","MAGENTA","PURPLE","WHITE","GRAY","DARK_GRAY","BLACK","BOLD","ITALIC","UNDERLINE",
                "STRIKE","RESET");
        int index=colores.indexOf(color);
        index=index+(10+2*(index/7));
        if(index==34){
            ItemStack reset=gui.getItem(34).clone();
            ItemMeta meta= reset.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+"RESET");
            reset.setItemMeta(meta);
            return reset;
        }
        return getColorToTextColor(gui.getItem(index));

    }
    private ItemStack getColorToTextColor(ItemStack item){
        String[] colorText=getColorName(item);
        ItemStack itemStack=item.clone();
        ItemMeta meta=itemStack.getItemMeta();
        if(colorText[1].equals("BOLD")||colorText[1].equals("ITALIC")||colorText[1].equals("UNDERLINE")||colorText[1].equals("STRIKE")){
            meta.setDisplayName("§f"+colorText[0]+colorText[1]);
        }else meta.setDisplayName(colorText[0]+colorText[1]);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
    private String[] getColorName(ItemStack item){
        String colorCode=item.getItemMeta().getDisplayName().substring(0,4);
        colorCode = mainPlugin.getColorTranslator().containsValue(colorCode.substring(2, 4))?colorCode.substring(2,4):colorCode.substring(0,2);
        String colorName=colorCode;
        for(String name: mainPlugin.getColorTranslator().keySet()){
            if(!mainPlugin.getColorTranslator().get(name).equals(colorCode))continue;
            colorName=name;break;
        }
        return new String[]{colorCode,colorName};
    }
    public void saveColor(ItemStack item,int slot) {
        String[] colorText;
        if(slot==34){
            colorText=new String[2];
            colorText[1]="RESET";
        }else colorText=getColorName(item);
        PluginCoder.getCoderGUI().getTextGUI().saveToTextGUI(colorText[1],false,false);
    }
    public Inventory getGUI() {
        return gui;
    }
}
