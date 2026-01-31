package berty.plugincoder.GUI.guis.commands;

import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandPromptGUI {

    private PluginCoder plugin;
    private Inventory gui;
    private Command command;
    private boolean isNewCommand=false;

    private CommandVarType newArgumentType=null;
    public CommandPromptGUI(PluginCoder plugin){
        this.plugin=plugin;
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> createInventory(), 2);
    }

    private void createInventory() {
        gui=Bukkit.createInventory(null,45," ");
        ItemStack blanco= new ItemStack(plugin.getCodeUtils().getVersionedMaterial("WHITE_STAINED_GLASS_PANE"));
        ItemMeta meta=blanco.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        blanco.setItemMeta(meta);
        ItemStack negro=plugin.getVersionNumber()<13?
                new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        meta=negro.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        negro.setItemMeta(meta);
        for(int i=0;i<7;i++){
            gui.setItem(19+i,blanco);
            gui.setItem(28+i,negro);
        }
        ItemStack backItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/cdc9e4dcfa4221a1fadc1b5b2b11d8beeb57879af1c42362142bae1edd5");
        meta=backItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        backItem.setItemMeta(meta);
        ItemStack nextItem=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/956a3618459e43b287b22b7e235ec699594546c6fcd6dc84bfca4cf30ab9311");
        meta=nextItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        nextItem.setItemMeta(meta);
        gui.setItem(18,backItem);gui.setItem(26,nextItem);
        updateInventoryLanguage();
        ItemStack commandItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("OAK_SIGN"));
        meta=commandItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+"");
        commandItem.setItemMeta(meta);
        gui.setItem(4,commandItem);
    }
    public void updateInventoryLanguage(){
        PluginCoder.getCoderGUI().createInventoryBase(gui,false);
        ItemStack removeArg=new ItemStack(Material.LAVA_BUCKET);
        ItemMeta meta=removeArg.getItemMeta();
        meta.setDisplayName(ChatColor.GRAY+PluginCoder.getCoderGUI().getGuiText("deleteArg"));
        removeArg.setItemMeta(meta);
        gui.setItem(23,removeArg);
        ItemStack addArg=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("MAP"));
        meta=addArg.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addArg"));
        addArg.setItemMeta(meta);
        gui.setItem(21,addArg);
        ItemStack text=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("WRITABLE_BOOK"));
        meta=text.getItemMeta();
        meta.setDisplayName(ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("addTextVar"));
        text.setItemMeta(meta);
        ItemStack math=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/1d1a3c96562348527d5798f291609281f72e16d611f1a76c0fa7abe043665");
        meta=math.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("addNumberVar"));
        math.setItemMeta(meta);
        ItemStack bool=PluginCoder.getCoderGUI().getPlayerHead("http://textures.minecraft.net/texture/bc8ea1f51f253ff5142ca11ae45193a4ad8c3ab5e9c6eec8ba7a4fcb7bac40");
        meta=bool.getItemMeta();
            meta.setDisplayName(ChatColor.WHITE+PluginCoder.getCoderGUI().getGuiText("addBooleanVar"));
        bool.setItemMeta(meta);
        ItemStack player=plugin.getVersionNumber()<13?new ItemStack(Material.getMaterial("SKULL_ITEM"),1,(short)3):
                new ItemStack(Material.PLAYER_HEAD);
        meta=player.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("addPlayerVar"));
        player.setItemMeta(meta);
        gui.setItem(28,player);gui.setItem(30,text);gui.setItem(32,math);gui.setItem(34,bool);
    }

    public void updateGUI(Command command){
        if(command!=null){
            this.command=command;
            isNewCommand=false;
            renderBar();
        }else{
            this.command=new Command(plugin.getSelectedPlugin());
            for(int i=0;i<7;i++)gui.setItem(10+i,null);
            isNewCommand=true;
        }
    }

    public void renderBar() {
        List<String> commandContents=new ArrayList<>(Arrays.asList(command.getPrompt().split(" ")));
        for(int i=0;i<7;i++)gui.setItem(10+i,null);
        for(int i=0;i<7;i++){
            if(commandContents.size()==i)break;
            if(commandContents.get(i).trim().isEmpty())continue;
            gui.setItem(10+i,getContentItem(commandContents.get(i)));
        }
        ItemStack commandItem=gui.getItem(4);
        ItemMeta meta=commandItem.getItemMeta();
        String promptTitle= "/"+command.getPrompt();
        for(String promptVar:command.getCommandVars().keySet()){
            promptTitle=promptTitle.replace(promptVar,ChatColor.YELLOW+promptVar+ChatColor.WHITE+"");
        }
        meta.setDisplayName(ChatColor.WHITE+promptTitle);
        commandItem.setItemMeta(meta);
        gui.setItem(4,commandItem);
    }

    private ItemStack getContentItem(String arg) {
        ItemStack argItem;
        if (command.getCommandVars().containsKey(arg)) {
            CommandVarType type=command.getCommandVars().get(arg);
            if(type==CommandVarType.PLAYER)argItem=gui.getItem(28).clone();
            else if(type==CommandVarType.TEXT)argItem=gui.getItem(30).clone();
            else if(type==CommandVarType.NUMBER)argItem=gui.getItem(32).clone();
            else argItem=gui.getItem(34).clone();
        }else argItem=new ItemStack(plugin.getCodeUtils().getVersionedMaterial("FILLED_MAP"));
        ItemMeta meta=argItem.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE+arg);
        argItem.setItemMeta(meta);
        return argItem;
    }
    public boolean saveChanges(Player p){
        if(isNewCommand){
            if(command.getPrompt()!=null&&!command.getPrompt().trim().isEmpty())PluginCoder.getCoderGUI().getCommandsGUI().addCommand(command);
            p.openInventory(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().get(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().size()-1));
        }else{
            PluginCoder.getCoderGUI().getCommandsGUI().updateCommand();
            PluginCoder.getCoderGUI().getCommandsGUI().openCommandPage(p);
        }
        return true;
    }

    public Inventory getGUI() {
        return gui;
    }

    public Command getCommand() {
        return command;
    }

    public CommandVarType getNewArgumentType() {
        return newArgumentType;
    }

    public void setNewArgumentType(CommandVarType newArgumentType) {
        this.newArgumentType = newArgumentType;
    }

    public void addNewArgument(CommandVarType commandVarType, Player p) {
        if(command.getPrompt().isEmpty()&&commandVarType!=CommandVarType.NONE) return;
        newArgumentType=commandVarType;
        p.closeInventory();
        if(commandVarType!=CommandVarType.NONE)p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeVarName"));
        else p.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeArgName"));
        PluginCoder.getCoderGUI().buttonSound(p);
    }
}
