package berty.plugincoder.GUI.guis.objects;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.GUI.InicVars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectGUI {

    private PluginCoder plugin;
    private PluginObject object;
    private Inventory gui;
    private int renderedPage;
    private int renderedSlot;
    private int selectedIndex;
    private boolean deletingItem=false;
    private List<ItemStack> renderedItems=new ArrayList<>();
    private boolean isAddingItem=false;
    public ObjectGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui= Bukkit.createInventory(null,54, " ");
        PluginCoder.getCoderGUI().createUpperLineInventory(gui,true);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<=3;i++)gui.setItem(10+i*2,negro);
        updateInventoryLanguage();
    }
    public void updateInventoryLanguage(){
        gui.setItem(0,PluginCoder.getCoderGUI().getReturnItem());
        gui.setItem(8,PluginCoder.getCoderGUI().getHomeItem());
        gui.setItem(45,PluginCoder.getCoderGUI().getBackItem());
        gui.setItem(53,PluginCoder.getCoderGUI().getNextItem());
        ItemStack properties=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=properties.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("properties"));
        properties.setItemMeta(meta);
        gui.setItem(11,properties);
        ItemStack functions=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        meta=functions.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED+PluginCoder.getCoderGUI().getGuiText("functions"));
        functions.setItemMeta(meta);
        gui.setItem(13,functions);
        ItemStack constructors=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.CRAFTING_TABLE));
        meta=constructors.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("constructors"));
        constructors.setItemMeta(meta);
        gui.setItem(15,constructors);
    }
    public void updateGUI(PluginObject object){
        ItemStack parent=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.CHEST));
        ItemMeta meta=parent.getItemMeta();
        String heritage=object.getParent()!=null?ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("objectParent").replace("%object%",ChatColor.YELLOW+object.getParent().getName()):
                ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("noObjectParent");
        meta.setDisplayName(heritage);
        parent.setItemMeta(meta);
        gui.setItem(4,parent);
        InicVars.functionType="PluginObject."+object.getName();
        renderedPage=0;
        renderedSlot=0;
        this.object=object;
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        ItemStack[] guiItems=gui.getContents().clone();
        gui= Bukkit.createInventory(null,54, ChatColor.translateAlternateColorCodes('&',"&f&l"+object.getName().toUpperCase()));
        gui.setContents(guiItems);
        displayProperties();
    }
    public boolean displayProperties(){
        if(renderedSlot==11)return false;
        renderedSlot=11;renderedPage=0;
        deletingItem=false;
        renderedItems.clear();
        ItemStack addProperty=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=addProperty.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addProperty"));
        addProperty.setItemMeta(meta);
        ItemStack removeProperty=new ItemStack(Material.BUCKET);
        meta=removeProperty.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteProperty"));
        removeProperty.setItemMeta(meta);
        gui.setItem(48,addProperty);gui.setItem(50,removeProperty);
        boolean plugin=object.equals(this.plugin.getSelectedPlugin().getMainObject());
        for(String property:object.getDeclaredProperties().stream().sorted().collect(Collectors.toList())){
            if(plugin&&property.equals("name"))continue;
            ItemStack propertyItem=getPropertyItem(property,object.getPropertyEqualities().get(property));
            renderedItems.add(propertyItem);
        }
        displayItems();
        return true;
    }

    public ItemStack getPropertyItem(String property,String equality) {
        ItemStack propertyItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
        ItemMeta meta=propertyItem.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+property);
        if(equality!=null&&!equality.isEmpty()){
            List<String> lore=new ArrayList<>();
            lore.add(ChatColor.translateAlternateColorCodes('&',"&f"+PluginCoder.getCoderGUI().putTextColor(equality)));
            meta.setLore(lore);
        }
        propertyItem.setItemMeta(meta);
        return propertyItem;
    }

    public boolean displayFunctions(){
        if(renderedSlot==13)return false;
        renderedSlot=13;renderedPage=0;
        renderedItems.clear();
        deletingItem=false;
        ItemStack addFunction=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK));
        ItemMeta meta=addFunction.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED+PluginCoder.getCoderGUI().getGuiText("addFunction"));
        addFunction.setItemMeta(meta);
        ItemStack removeFunction=new ItemStack(Material.BUCKET);
        meta=removeFunction.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteFunction"));
        removeFunction.setItemMeta(meta);
        gui.setItem(48,addFunction);gui.setItem(50,removeFunction);
        for(String function:object.getFunctions()){
            ItemStack functionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(function);
            renderedItems.add(functionItem);
        }
        displayItems();
        return true;
    }
    public boolean displayConstructors(){
        if(renderedSlot==15)return false;
        renderedSlot=15;renderedPage=0;
        renderedItems.clear();
        deletingItem=false;
        ItemStack addConstructor=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.CRAFTING_TABLE));
        ItemMeta meta=addConstructor.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD+PluginCoder.getCoderGUI().getGuiText("addConstructor"));
        addConstructor.setItemMeta(meta);
        ItemStack removeConstructor=new ItemStack(Material.BUCKET);
        meta=removeConstructor.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteConstructor"));
        removeConstructor.setItemMeta(meta);
        gui.setItem(48,addConstructor);gui.setItem(50,removeConstructor);
        for(String constructor:object.getConstructors()){
            ItemStack constructorItem=getConstructorItem(constructor);
            renderedItems.add(constructorItem);
        }
        displayItems();
        return true;
    }

    private ItemStack getConstructorItem(String constructor) {
        ItemStack constructorItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial(Material.CRAFTING_TABLE));
        String constructorName=constructor.replaceAll("^"+object.getName()+"\\(([^(]*)\\)(.+)$",object.getName()+"($1)");
        if(constructorName.equals(constructor))constructorName=object.getName();
        constructorItem=PluginCoder.getCoderGUI().getFunctionItem(constructor,constructorItem,ChatColor.GOLD+constructorName);
        return constructorItem;
    }

    private void displayItems() {
        int index=0;
        for(int i=0;i<7;i++){
            gui.setItem(28+i,null);
            gui.setItem(37+i,null);
        }
        List<ItemStack> pageElements;
        if(renderedItems.size()>14*(renderedPage+1))pageElements=renderedItems.subList(14*renderedPage,14*(renderedPage+1));
        else pageElements=renderedItems.subList(14*renderedPage,renderedItems.size());
        for(ItemStack element:pageElements){
            gui.setItem(28+index,element);
            if(index==6)index+=3;
            else index++;
        }
    }
    public boolean previousPage(){
        if(renderedPage>0)renderedPage--;
        else return false;
        displayItems();
        return true;
    }
    public void saveToObjectGUI(String instruction){
        int slot=selectedIndex%14;
        slot+=slot<7?28:30;
        String oldPropertyName=ChatColor.stripColor(gui.getItem(slot).getItemMeta().getDisplayName()).trim();
        String propertyName=instruction.replaceAll("^([^=]+)=(.*)$","$1");
        String equality=instruction.replaceAll("^([^=]+)=(.*)$","$2");
        if(!oldPropertyName.equals(propertyName)){
            object.getDeclaredPropertyEqualities().remove(oldPropertyName);
            object.removeProperty(oldPropertyName);
        }
        for(String property:object.getDeclaredProperties()){
            if(property.equals(propertyName)&&!oldPropertyName.equals(propertyName)){
                ErrorManager.errorPropertyName(propertyName);
                propertyName=oldPropertyName;break;
            }
        }
        ItemStack propertyItem;
        ErrorManager.setSender(PluginCoder.getCoderGUI().getGuiPlayer());
        plugin.getCodeExecuter().executeInstruction(equality,instruction, InicVars.getInicVars(plugin.getSelectedPlugin(),""));
        ErrorManager.setSender(Bukkit.getConsoleSender());
        if(PluginCoder.isErrorFound()){
            object.getDeclaredPropertyEqualities().remove(propertyName);
            propertyItem=getPropertyItem(propertyName,"");
        }else {
            object.getDeclaredPropertyEqualities().put(propertyName,equality);
            propertyItem=getPropertyItem(propertyName,equality);
        }
        renderedItems.set(selectedIndex,propertyItem);
        gui.setItem(slot,propertyItem);
    }
    public boolean nextPage(){
        if(renderedItems.size()>(renderedPage+1)*14)renderedPage++;
        else return false;
        displayItems();
        return true;
    }
    public Inventory getGUI() {
        return gui;
    }

    public void updateSelectedIndex(int slot) {
        selectedIndex=(slot<35?slot-28:slot-30)+renderedPage*14;
    }
    public void updateSelectedFunction() {
        ItemStack functionItem;
        if(renderedSlot==13){
            String function=object.getFunctions().get(selectedIndex);
            functionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(function);
        }
        else {
            String constructor=object.getConstructors().get(selectedIndex);
            functionItem=getConstructorItem(constructor);
        }
        int slot=selectedIndex%14;
        slot+=slot<7?28:30;
        gui.setItem(slot,functionItem);
    }
    public void addItem(Player p){
        if(renderedSlot==11){
            isAddingItem=true;
            p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writePropName"));
        }else if(renderedSlot==13){
            isAddingItem=true;
            p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeFuncName"));
        }else{
           object.getConstructors().add(object.getName()+"{}");
           ItemStack constructorItem=getConstructorItem(object.getName()+"{}");
           renderedItems.add(constructorItem);
           displayItems();return;
        }
        p.closeInventory();
    }
    public void deleteItem(int slot) {
        int index=(slot<35?slot-28:slot-30)+renderedPage*14;
        String propertyName=ChatColor.stripColor(gui.getItem(slot).getItemMeta().getDisplayName());
        if(renderedSlot==11)object.removeProperty(propertyName);
        else if(renderedSlot==13)object.getDeclaredFunctions().remove(index);
        else object.getConstructors().remove(index);
        renderedItems.remove(index);
        displayItems();
    }
    public void addItem(ItemStack item){
        renderedItems.add(item);
        displayItems();
    }
    public PluginObject getObject() {
        return object;
    }

    public boolean isAddingItem() {
        return isAddingItem;
    }

    public void setAddingItem(boolean addingProperty) {
        isAddingItem = addingProperty;
    }

    public boolean isDeletingItem() {
        return deletingItem;
    }

    public void setDeletingItem(boolean deletingItem) {
        this.deletingItem = deletingItem;
    }

    public int getRenderedSlot() {
        return renderedSlot;
    }

    public int getRenderedPage() {
        return renderedPage;
    }
}
