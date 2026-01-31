package berty.plugincoder.GUI.guis.commands;

import berty.plugincoder.interpreter.command.Command;
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

public class CommandsGUI{

    private PluginCoder mainPlugin;
    private List<Inventory> commands=new ArrayList<>();
    private boolean isEditingPrompt;
    private int commandEditedIndex;

    private boolean deleteCommand=false;
    public CommandsGUI(PluginCoder plugin){
        this.mainPlugin =plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> updateGUI(), 2);
    }
    public void updateGUI(){
        int index=0;
        commands.clear();
        ItemStack flecha=new ItemStack(Material.ARROW);
        ItemMeta meta=flecha.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        flecha.setItemMeta(meta);
        addNewInventory();
        for(Command command: mainPlugin.getSelectedPlugin().getCommands()){
            renderCommand(command,index,flecha,true);
            index++;
        }
    }
    private void renderCommand(Command command,int index,ItemStack flecha,boolean renderBoth){
        if(renderBoth&&index!=0&&index%4==0) addNewInventory();
        if(renderBoth||isEditingPrompt){
            ItemStack prompt=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
            ItemMeta meta=prompt.getItemMeta();
            String promptTitle= "/"+command.getPrompt();
            for(String promptVar:command.getCommandVars().keySet()){
                promptTitle=promptTitle.replace(promptVar,ChatColor.YELLOW+promptVar+ChatColor.WHITE+"");
            }
            meta.setDisplayName(ChatColor.WHITE+promptTitle);
            prompt.setItemMeta(meta);
            commands.get(index/4).setItem(11+(index%4)*9,prompt);
        }
        if(renderBoth||!isEditingPrompt){
            ItemStack function= PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(command.getFunction());
            ItemMeta meta=function.getItemMeta();
            meta.setDisplayName(ChatColor.DARK_RED+"function");//TODO cambiar por cada idioma
            function.setItemMeta(meta);
            commands.get(index/4).setItem(15+(index%4)*9,function);
        }
        if(!renderBoth)return;
        commands.get(index/4).setItem(13+(index%4)*9,flecha);
    }
    private void addNewInventory() {
        Inventory inventory=Bukkit.createInventory(null,54,ChatColor.translateAlternateColorCodes('&',"&f&l"+PluginCoder.getCoderGUI().getGuiText("commandsTitle").toUpperCase()));
        PluginCoder.getCoderGUI().createInventoryBase(inventory,true);
        ItemStack negro= mainPlugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<4;i++)for(int y=0;y<4;y++)inventory.setItem(10+i*9+y*2,negro);
        ItemStack addCommand=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial("MAP"));
        meta=addCommand.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addCommand"));
        addCommand.setItemMeta(meta);
        ItemStack removeCommand=new ItemStack(Material.BUCKET);
        meta=removeCommand.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("removeCommand"));
        removeCommand.setItemMeta(meta);
        inventory.setItem(26,addCommand);inventory.setItem(35,removeCommand);
        commands.add(inventory);
    }

    public void addCommand(Command command){
        int numCommands= mainPlugin.getSelectedPlugin().getCommands().size();
        ItemStack flecha=new ItemStack(Material.ARROW);
        ItemMeta meta=flecha.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        flecha.setItemMeta(meta);
        mainPlugin.getSelectedPlugin().getCommands().add(command);
        renderCommand(command,numCommands,flecha,true);
    }
    public void updateCommand(){
        Command command= mainPlugin.getSelectedPlugin().getCommands().get(commandEditedIndex);
        ItemStack flecha=new ItemStack(Material.ARROW);
        ItemMeta meta=flecha.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        flecha.setItemMeta(meta);
        renderCommand(command,commandEditedIndex,flecha,true);
    }
    public void openCommandPage(Player p) {
        p.openInventory(commands.get(commandEditedIndex/4));
    }
    public void removeCommand(Player p,Inventory inventory,int slot){
        int inventoryIndex=commands.indexOf(inventory);
        int index=inventoryIndex*4+(slot/9)-1;
        Command command= mainPlugin.getSelectedPlugin().getCommands().get(index);
        mainPlugin.getSelectedPlugin().removeCommand(command);
        int lineIndex=slot/9;
        inventory.setItem(2+9*lineIndex,null);inventory.setItem(4+9*lineIndex,null);
        inventory.setItem(6+9*lineIndex,null);lineIndex--;
        for(int i = index; i<= mainPlugin.getSelectedPlugin().getCommands().size(); i++){
            Inventory inv=commands.get(inventoryIndex);lineIndex++;
            if(lineIndex%4==0){lineIndex=0;inventoryIndex++;
            if(inventoryIndex==commands.size())break;}
            Inventory nextInv=commands.get(inventoryIndex);
            int oldLineIndex=lineIndex-1<0?3:(lineIndex-1);
            inv.setItem(11+9*oldLineIndex,nextInv.getItem(11+9*lineIndex));nextInv.setItem(11+9*lineIndex,null);
            inv.setItem(13+9*oldLineIndex,nextInv.getItem(13+9*lineIndex));nextInv.setItem(13+9*lineIndex,null);
            inv.setItem(15+9*oldLineIndex,nextInv.getItem(15+9*lineIndex));nextInv.setItem(15+9*lineIndex,null);
        }
        p.updateInventory();
        int size= mainPlugin.getSelectedPlugin().getCommands().size();
        if(size%4==0&&size!=0){
            if(inventory.equals(commands.get(commands.size()-1))) p.openInventory(commands.get(commands.size()-2));
            commands.remove(commands.size()-1);
        }
    }
    public Command updateCommandEdtitedIndexes(Inventory inventory, int slot){
        commandEditedIndex=commands.indexOf(inventory)*4+(slot/9)-1;
        int promtOrFunction=slot-4+9*(slot/9);
        if(promtOrFunction>0)isEditingPrompt=false;
        else isEditingPrompt=true;
        return mainPlugin.getSelectedPlugin().getCommands().get(commandEditedIndex);
    }
    public List<Inventory> getGUI() {
        return commands;
    }
    public boolean isDeleteCommand() {
        return deleteCommand;
    }

    public void setDeleteCommand(boolean deleteCommand) {
        this.deleteCommand = deleteCommand;
    }
}
