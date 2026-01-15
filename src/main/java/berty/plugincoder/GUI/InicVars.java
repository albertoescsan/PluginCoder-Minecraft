package berty.plugincoder.GUI;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import berty.plugincoder.predictor.PredictType;

import java.lang.reflect.Constructor;
import java.util.*;

public class InicVars {

    public static PluginCoder mainPlugin;
    public static String functionType="";
    public static Map<String, Object> getInicVars(Plugin plugin, String function) {
        Map<String,Object> inicVars=mainPlugin.getPluginVars(plugin);
        inicVars.put("plugin",plugin.getMainObject().getInstance());
        boolean adFunction=function.startsWith("Activation{")||function.startsWith("Deactivation{");
        if(functionType.equals("event"))inicVars.put("event",eventVar(function));
        else if(functionType.equals("command"))inicVars.putAll(getCommandVars(function));
        else if(functionType.startsWith("PluginObject.")||adFunction){
            PluginObject object=plugin.getObject(adFunction?"Plugin":functionType.replace("PluginObject.",""));
            inicVars.put("this",object.getInstance());
            if(object.getName().equals(plugin.getMainObject().getName()))inicVars.remove("plugin");
            for(String property:object.getProperties()){
                String equality=object.getPropertyEqualities().get(property);
                if(equality!=null){
                    inicVars.put(property,mainPlugin.getCodeExecuter().executeInstruction(equality,"",inicVars));
                }
                else inicVars.put(property,getExampleValue(property, PredictType.predictTypeOfObjectProperty(object,property)));
            }
            if(!function.matches("^([^({]+)\\(([^;]+)\\)\\{(.+)$"))return inicVars;
            List<String> functionParams=mainPlugin.getCodeExecuter().getStringParameters(
                    function.replaceAll("^([^(]+)\\(([^;]+)\\)\\{(.+)$","($2)"));
            for(String parameter:functionParams){ //precedir tipos de parametros
                inicVars.put(parameter,getExampleValue(parameter,PredictType.predictTypeOfVar(plugin,parameter,function,new HashSet<>(inicVars.keySet()))));
            }
        }
        return inicVars;
    }
    public static Map<String,String> getInicVarTypes(Plugin plugin,String function,String functionType) {
        String functionTypeTemp=InicVars.functionType;
        InicVars.functionType=functionType;
        Map<String, String> variableTypes=getInicVarTypes(plugin,function);
        InicVars.functionType=functionTypeTemp;
        return variableTypes;
    }
    public static Map<String, String> getInicVarTypes(Plugin plugin,String function) {
        Map<String,String> inicVarTypes=mainPlugin.getPluginVarTypes();
        if(functionType.equals("event"))inicVarTypes.put("event",eventVar(function).getClass().getTypeName());
        else if(functionType.equals("command")){
            Map<String,Object> commandVars= getCommandVars(function);
            for(String var:commandVars.keySet())inicVarTypes.put(var,commandVars.get(var).getClass().getTypeName());
            inicVarTypes.put("sender",CommandSender.class.getTypeName());
        }
        else if(functionType.startsWith("PluginObject.")){
            inicVarTypes.put("this",functionType);
            PluginObject object=plugin.getObject(functionType.replace("PluginObject.",""));
            if(object.getName().equals(plugin.getMainObject().getName()))inicVarTypes.remove("plugin");
            //predecir tipo de las propiedades del objeto
            for(String property:object.getProperties())inicVarTypes.put(property,PredictType.predictTypeOfObjectProperty(object,property));
            if(!function.matches("^([^(]+)\\(([^;]+)\\)\\{(.+)$"))return inicVarTypes;
            List<String> functionParams=mainPlugin.getCodeExecuter().getStringParameters(function.replaceAll("^([^{]+)\\s*\\{(.+)$","$1"));
            Set<String> predictParams=new HashSet<>(object.getProperties());
            predictParams.addAll(functionParams);
            for(String parameter:functionParams){ //precedir tipos de parametros
                inicVarTypes.put(parameter,PredictType.predictTypeOfVar(plugin,parameter, function,predictParams));
            }
        }
        return inicVarTypes;
    }
    private static Object eventVar(String function){
        String eventName=function.replaceAll("^([^{]+)\\s*\\{(.+)$","$1");
        Player p=PluginCoder.getCoderGUI().getGuiPlayer();
        if(eventName.equals("onPlayerJoin"))return new PlayerJoinEvent(p, "join message");
        else if(eventName.equals("onPlayerLeave"))return new PlayerQuitEvent(p,"leave message");
        else if(eventName.equals("onPlayerClick")) {
            Block block = p.getWorld().getBlockAt(p.getLocation());
            return new PlayerInteractEvent(p, Action.RIGHT_CLICK_BLOCK, p.getItemInHand(), block, BlockFace.NORTH);
        }else if(eventName.equals("onPlayerMove"))return new PlayerMoveEvent(p,p.getLocation(),p.getLocation());
        else if(eventName.equals("onPlayerTeleport"))return new PlayerTeleportEvent(p,p.getLocation(),p.getLocation());
        else if(eventName.equals("onBlockPlace")) {
                Block block = p.getWorld().getBlockAt(p.getLocation());
                return new BlockPlaceEvent(block, block.getState(), block, p.getItemInHand(), p, true);
        }else if(eventName.equals("onBlockBreak")) {
                Block block = p.getWorld().getBlockAt(p.getLocation());
                return new BlockBreakEvent(block, p);
        }else if(eventName.equals("onInventoryClick"))return new InventoryClickEvent(p.getOpenInventory(),
                InventoryType.SlotType.CONTAINER,0, ClickType.LEFT, InventoryAction.NOTHING);
        else if(eventName.equals("onPlayerChat"))return new PlayerChatEvent(p,"Hello");
        else if(eventName.equals("onPlayerDie")){
            if(mainPlugin.getVersionNumber()<21)return new PlayerDeathEvent(p,Arrays.asList(new ItemStack(Material.GRASS_BLOCK)),0,"You died");
            else {
               try {//DamageSource.builder(DamageType.GENERIC).build();
                   Class damageSourceClass=Class.forName("org.bukkit.damage.DamageSource");
                   Class damageSourceType=Class.forName("org.bukkit.damage.DamageType");
                   Object damageType=damageSourceType.getField("GENERIC").get(null);
                   Object builderInstance=damageSourceClass.getMethod("builder",damageSourceType).invoke(null,damageType);
                   Object damageSource=builderInstance.getClass().getMethod("build").invoke(builderInstance);
                   Constructor constructor = PlayerDeathEvent.class.getConstructor(Player.class, damageSourceClass, List.class, int.class, String.class);
                   return constructor.newInstance(p,damageSource, Arrays.asList(new ItemStack(Material.GRASS_BLOCK)), 0, "You died");
               }catch (Exception e){e.printStackTrace();}
            }
        }
        //TODO añadir más eventos
        return null;
    }
    private static Map<String, Object> getCommandVars(String function) {
        Command command=getCommandByFunction(function);
        return getCommandVars(command,"");
    }
    public static Command getCommandByFunction(String function) {
        int commandIndex=Integer.parseInt(function.replaceAll("^command([^{]+)\\{(.*)\\}$","$1"));
        if(mainPlugin.getSelectedPlugin().getCommands().size()<=commandIndex)return null;
        return mainPlugin.getSelectedPlugin().getCommands().get(commandIndex);
    }
    public static Map<String,Object> getCommandVars(Command command,String prompt){
        ErrorManager.setSender(PluginCoder.getCoderGUI().getGuiPlayer());
        Map<String, Object> variables=new HashMap<>();
        Player p=PluginCoder.getCoderGUI().getGuiPlayer();
        variables.put("sender", (CommandSender)p);
        String[] promptElements=prompt.split(" ");
        String[] commandElements=command.getPrompt().split(" ");
        for(int i=0;i<commandElements.length;i++){
            String promptElement=prompt.trim().isEmpty()?"":promptElements[i];
            String commandElement=commandElements[i];
            if(!command.getCommandVars().containsKey(commandElement))continue;
            if(command.getCommandVars().get(commandElement)== CommandVarType.PLAYER){
                if(prompt.trim().isEmpty())variables.put(commandElement,p);
                else {
                    Player player=Bukkit.getPlayer(promptElement);
                    if(player==null) {
                        ErrorManager.errorPlayerNotFound(promptElement);return null;
                    }else variables.put(commandElement,player);
                }
            }else if(command.getCommandVars().get(commandElement)== CommandVarType.NUMBER){
                if(prompt.trim().isEmpty())variables.put(commandElement,0);
                else {
                    try{
                        double number=Double.parseDouble(promptElement);
                        variables.put(commandElement,number);
                    }catch (Exception e){
                        ErrorManager.isNotNumber(promptElement,"");
                    }
                }
            }else if(command.getCommandVars().get(commandElement)== CommandVarType.BOOLEAN){
                if(prompt.trim().isEmpty())variables.put(commandElement,true);
                else {
                    if(promptElement.equalsIgnoreCase("true"))variables.put(commandElement,true);
                    else if(promptElement.equalsIgnoreCase("false"))variables.put(commandElement,false);
                    else ErrorManager.isNotBoolean(promptElement,"");
                }
            }else{
                if(prompt.isEmpty())variables.put(commandElement,commandElement);
                else variables.put(commandElement,promptElement);
            }
        }
        ErrorManager.setSender(Bukkit.getConsoleSender());
        return variables;
    }
    private static Object getExampleValue(String var,String type){
        if(type.equals(String.class.getTypeName()))return var;
        if(type.equals("int"))return 0;
        if(type.equals("double"))return 0.;
        if(type.equals(List.class.getTypeName()))return Arrays.asList("example");
        if(type.equals(Map.class.getTypeName())){
            Map<Object,Object> map=new HashMap<>();
            map.put("key","value");return map;
        }
        //TODO añadir más valores
        return null;
    }
}
