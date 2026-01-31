package berty.plugincoder.GUI.guis.text;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TextGUI {
    private PluginCoder plugin;
    private Inventory gui;
    private List<ItemStack> colores=new ArrayList<>();
    private int colorTaskId;
    private List<Inventory> previousInvs=new ArrayList<>();
    private List<String> renderedInstructions=new ArrayList<>();
    private List<Boolean> isNewText=new ArrayList<>();
    private boolean isAddingText=false;
    private boolean isDeletingText=false;
    private int startContentIndex=0;
    private List<String> textContents=new ArrayList<>();
    private List<Integer> lastIndex=new ArrayList<>();
    public TextGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 3);
    }

    private void createInventory() {
        gui=Bukkit.createInventory(null,54," ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,false);
        ItemStack backItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
        ItemMeta meta=backItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        backItem.setItemMeta(meta);
        ItemStack nextItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
        meta=nextItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        nextItem.setItemMeta(meta);
        gui.setItem(18,backItem);
        gui.setItem(26,nextItem);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<7;i++){
            gui.setItem(28+i,negro);
            gui.setItem(28+9+i,negro);
        }
        ItemStack instructionItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        gui.setItem(4,instructionItem);
        ItemStack openParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/674153bb6a3fa8979a889f99aa0fc84ba9f427ff0d8e487f76fa6c8831d18e9");
        meta=openParentesis.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"(");
        openParentesis.setItemMeta(meta);
        ItemStack closeParentesis=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/9c60e240cda0d9bb315b385119e5a9482e885827baf018e1c03b6a6d612d3f");
        meta=closeParentesis.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+")");
        closeParentesis.setItemMeta(meta);
        gui.setItem(39,openParentesis);gui.setItem(41,closeParentesis);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage() {
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        ItemStack vars=new ItemStack(Material.CHEST);
        ItemMeta meta=vars.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("variablesTitle"));
        vars.setItemMeta(meta);
        gui.setItem(33,vars);
        ItemStack writeText=new ItemStack(Material.FEATHER);
        meta=writeText.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("writeText"));
        writeText.setItemMeta(meta);
        gui.setItem(31,writeText);
        ItemStack space=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(" ");
        meta=space.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addSpace"));
        space.setItemMeta(meta);
        gui.setItem(40,space);
        ItemStack delete=new ItemStack(Material.BUCKET);
        meta=delete.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteText"));
        delete.setItemMeta(meta);gui.setItem(22,delete);
        colores.clear();
        String colorText=PluginCoder.getCoderGUI().getGuiText("textColor");
        ItemStack rojo=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("DARK_RED");
        ItemStack naranja=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("ORANGE");
        ItemStack amarillo=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("YELLOW");
        ItemStack verde=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("GREEN");
        ItemStack celeste=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("CYAN");
        ItemStack azul=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("DARK_BLUE");
        ItemStack morado=PluginCoder.getCoderGUI().getTextColorGUI().getColorItem("PURPLE");
        colores.add(putColorTextName(rojo,ChatColor.DARK_RED,colorText));colores.add(putColorTextName(naranja,ChatColor.GOLD,colorText));
        colores.add(putColorTextName(amarillo,ChatColor.YELLOW,colorText));colores.add(putColorTextName(verde,ChatColor.GREEN,colorText));
        colores.add(putColorTextName(celeste,ChatColor.AQUA,colorText));colores.add(putColorTextName(azul,ChatColor.DARK_BLUE,colorText));
        colores.add(putColorTextName(morado,ChatColor.DARK_PURPLE,colorText));
    }
    private ItemStack putColorTextName(ItemStack item, ChatColor color,String text){
        ItemMeta meta=item.getItemMeta();
        meta.setDisplayName(color+text);
        item.setItemMeta(meta);
        return item;
    }
    public void updateInventory(String instruction,Inventory inventory){
        isDeletingText=false;
        gui.getItem(22).setType(Material.BUCKET);
        previousInvs.add(inventory);
        renderedInstructions.add(instruction);
        updateInventory(instruction);
    }
    private void updateInventoryWithPreviousInstruction(){
        updateInventory(renderedInstructions.get(renderedInstructions.size()-1));
    }
    private void updateInventory(String instruction){
        textContents.clear();
        startContentIndex=0;
        cancelColorTask();
        String text="";
        PluginCoder.getCoderGUI().getExecutionWriterGUI().updateVariables();
        Set<String> variables=PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables();
        int parenthesisCount=0;
        for(Character c:instruction.toCharArray()){
            if(c.equals('(')){
                parenthesisCount++;
                String methodName=text.replaceAll("^(.*)\\.([^.(]+)$","$2");
                if(!variables.contains(methodName.split("\\.")[0])&&parenthesisCount==1){
                    textContents.add(text);textContents.add("(");
                    text="";continue;
                }
            }else if(c.equals(')')){
                parenthesisCount--;
                String methodName=text.replaceAll("^(.*)\\.([^.(]+)\\((.*)$","$2");
                if(!variables.contains(methodName.split("\\.")[0])&&parenthesisCount==0){
                    textContents.add(text);textContents.add(")");
                    text="";continue;
                }
            }
            if(c.equals('+')&&parenthesisCount==0){
                textContents.add(text);
                text="";
                continue;
            }
            text+=c;
        }
        if(!text.isEmpty())textContents.add(text);
        renderBar();
        startColorTask();
    }
    public Inventory getGUI() {
        return gui;
    }

    public boolean saveChanges(boolean closingInventoryWithoutReturning) {
        String newInstruction=ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()).trim();
        Inventory inv=previousInvs.remove(previousInvs.size()-1);
        renderedInstructions.remove(renderedInstructions.size()-1);
        cancelColorTask();
        if(inv.equals(PluginCoder.getCoderGUI().getSetValueGUI().getGUI())) {
            PluginCoder.getCoderGUI().getSetValueGUI().saveToSetGuiPage(newInstruction,closingInventoryWithoutReturning);
        }else if(inv.equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI())){
            PluginCoder.getCoderGUI().getEqualityGUI().saveToEqualityGUI(newInstruction,closingInventoryWithoutReturning);
        }else if(inv.equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            PluginCoder.getCoderGUI().getParametersGUI().saveToParametersGUI(newInstruction,closingInventoryWithoutReturning);
        }
        //TODO añadir más inventarios
        return true;
    }
    public void startColorTask(){
        colorTaskId=Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            int index=colores.indexOf(gui.getItem(29));
            if(index+1==colores.size())index=-1;
            ItemStack newColor=colores.get(index+1);
            gui.setItem(29,newColor);
        },0,20);
    }
    public void cancelColorTask(){
        Bukkit.getScheduler().cancelTask(colorTaskId);
    }
    public void returnPage(Player p) {
        cancelColorTask();
        Inventory previousInv=previousInvs.get(previousInvs.size()-1);
        saveChanges(false);
        p.openInventory(previousInv);
    }
    public void returnHome(Player p) {
        cancelColorTask();
        saveChanges(true);
        renderedInstructions.clear();
        PluginCoder.getCoderGUI().returnHome(p,true);
    }
    public void renderBar() {
        for(int i=0;i<7;i++)gui.setItem(10+i,null);
        for(int i=0;i<7;i++){
            if(textContents.size()==i)break;
            gui.setItem(10+i,getContentItem(textContents.get(i+startContentIndex)));
        }
        String text="";
        for(String content:textContents){
            text+=content+(content.equals("(")?"":"+");
        }
        if(!text.endsWith("(")&&!text.isEmpty())text=text.substring(0,text.length()-1);
        ItemStack commandItem=gui.getItem(4);
        ItemMeta meta=commandItem.getItemMeta();
        meta.setDisplayName("§f"+PluginCoder.getCoderGUI().putTextColor(text));
        commandItem.setItemMeta(meta);
        gui.setItem(4,commandItem);
    }

    private ItemStack getContentItem(String text) {
        if(plugin.getColorTranslator().containsKey(text))return PluginCoder.getCoderGUI().getTextColorGUI().getColorItem(text);
        else if(text.equals("("))return gui.getItem(39);
        else if(text.equals(")"))return gui.getItem(41);
        else return PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(text);
    }
    public void addNewContent(String content) {
        textContents.add(content);
        if(textContents.size()>7){
            startContentIndex=textContents.size()-8;
            nextContent();
        }else renderBar();
        PluginCoder.getCoderGUI().getGuiPlayer().updateInventory();
    }
    public boolean nextContent(){
        if(startContentIndex+7<textContents.size())startContentIndex++;
        else return false;
        renderBar();
        return true;
    }
    public boolean previousContent(){
        if(startContentIndex>0)startContentIndex--;
        else return false;
        renderBar();
        return true;
    }
    public void saveToTextGUI(String instruction,boolean closingInventoryWithoutReturning,boolean updateInv) {
       if(updateInv)updateInventoryWithPreviousInstruction();
        if(textContents.size()==1&&textContents.get(0).equals("null")){
            textContents.set(0,instruction);
        }else if(isNewText.remove(isNewText.size()-1))addNewContent(instruction);
        else  textContents.set(lastIndex.remove(lastIndex.size()-1),instruction);
        renderBar();
        if(closingInventoryWithoutReturning)saveChanges(true);
    }
    public void prepareToNextGUI(){
        cancelColorTask();
        renderedInstructions.set(renderedInstructions.size()-1,ChatColor.stripColor(gui.getItem(4).getItemMeta().getDisplayName()));
    }
    public List<Boolean> getIsNewText() {
        return isNewText;
    }

    public boolean isAddingText() {
        return isAddingText;
    }

    public void setAddingText(boolean addingText) {
        isAddingText = addingText;
    }
    public List<String> getTextContents() {
        return textContents;
    }

    public int getStartContentIndex() {
        return startContentIndex;
    }

    public List<Integer> getLastIndex() {
        return lastIndex;
    }

    public boolean isDeletingText() {
        return isDeletingText;
    }

    public void setDeletingText(boolean deletingText) {
        isDeletingText = deletingText;
    }
}
