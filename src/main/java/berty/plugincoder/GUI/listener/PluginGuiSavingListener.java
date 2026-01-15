package berty.plugincoder.GUI.listener;

import org.bukkit.inventory.ItemStack;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PluginGuiSavingListener implements Listener {
    private PluginCoder mainPlugin;

    public PluginGuiSavingListener(PluginCoder plugin){
        mainPlugin=plugin;
    }
    @EventHandler
    public void alCerrarGUIS(InventoryCloseEvent event){//añadir mas inventarios cuando se vayan creando
        if(!(event.getPlayer() instanceof Player))return;
        Player player= (Player) event.getPlayer();
        //PluginCoderGui
        if(event.getInventory().equals(PluginCoder.getCoderGUI().getPluginCoderGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(mainPlugin.getEventsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(mainPlugin.getObjectsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getLanguagesGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getPluginsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getObjectGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios cuando sea necesario
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
            //cerrar language gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getLanguagesGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
            //cerrar plugin gui
        }else if(PluginCoder.getCoderGUI().getPluginsGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getPluginsGUI().getPluginEditor().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }
        //cerrar PluginEditor
        else if(event.getInventory().equals(PluginCoder.getCoderGUI().getPluginsGUI().getPluginEditor())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }
        //cerrar commands GUI
        else if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getCommandPromptGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                //TODO añadir mas inventarios cuando sea necesario
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }//cerrar commandPrompt GUI
        else if(PluginCoder.getCoderGUI().getCommandPromptGUI().equals(event.getInventory())){
            if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
            if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
            if(PluginCoder.getCoderGUI().getCommandPromptGUI().getNewArgumentType()!=null)return;
            PluginCoder.getCoderGUI().getCommandPromptGUI().saveChanges(player);
            PluginCoder.getCoderGUI().setGuiPlayer(null);
        }
        //cerrar objects GUI
        else if(mainPlugin.getObjectsGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getObjectsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getObjectGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios cuando sea necesario
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }//cerrar object GUI
        else if(mainPlugin.getObjectsGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getObjectsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getVariableGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                //TODO añadir mas inventarios cuando sea necesario
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }
        //cerrar events gui
        else if(mainPlugin.getEventsGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getEventsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getEventsGUI().getEventsItemsGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }
        //cerrar EventItemsGUI
        else if(mainPlugin.getEventsGUI().getEventsItemsGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getEventsGUI().getEventsItemsGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(mainPlugin.getEventsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                PluginCoder.getCoderGUI().setGuiPlayer(null);
            }, 1);
        }
        //cerrar functionGUI
        else if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->event.getInventory().equals(inv))){
            ItemStack itemInCursor=player.getItemOnCursor()!=null?player.getItemOnCursor().clone():null;
            int inventoryIndex=mainPlugin.getFunctionGUI().getGUI().indexOf(event.getInventory());
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                player.setItemOnCursor(null);player.getInventory().remove(itemInCursor);
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory()))){
                    player.setItemOnCursor(itemInCursor);return;
                }
                else{
                    if(mainPlugin.getFunctionGUI().getMoveInstructionIndex()>-1){//restablecer eliminacion de instruccion temporal de mover instruccion
                        int instructionSlot=mainPlugin.getFunctionGUI().getMoveInstructionIndex()%28;
                        instructionSlot=instructionSlot+(10+2*(instructionSlot/7));
                        mainPlugin.getFunctionGUI().insertInstructionItem(itemInCursor,inventoryIndex,instructionSlot);
                    }
                    mainPlugin.getFunctionGUI().setMoveInstructionIndex(-2);mainPlugin.getFunctionGUI().setCopyInstructionIndex(-2);
                }
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getEventsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(PluginCoder.getCoderGUI().getInstructionsGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getVariableGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getReturnGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getForParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getObjectGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios cuando sea necesario
                mainPlugin.getFunctionGUI().returnHome(player,false);
            }, 1);
            //cerrar variableGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getVariableGUI().getGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getVariableGUI().isEditingVarName())return;
                if(mainPlugin.getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getObjectGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //esta funcion guarda automaticamente las funciones si se cierra forzosamente el inventario
                PluginCoder.getCoderGUI().getVariableGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar ExecutionGUI
        }
        //cerrar ExecutionWriterGUI
        else if(event.getInventory().equals(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getTextGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI()))return;
                //TODO añadir mas inventarios cuando sea necesario
                //esta funcion guarda automaticamente las funciones si se cierra forzosamente el inventario
                mainPlugin.getExecutionWriterGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar InstructionsGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(PluginCoder.getCoderGUI().getInstructionsGUI().getConditionalsGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getInstructionsGUI().getLoopsGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getVariableGUI().isEditingVarName())return;
                //añadir mas inventarios a los que pueda acceder esta gui
                mainPlugin.getFunctionGUI().saveChanges(false);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar InstructionConditionalsGui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getConditionalsGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getInstructionsGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                //añadir mas inventarios a los que pueda acceder esta gui
                mainPlugin.getFunctionGUI().saveChanges(false);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar InstructionLoopsGui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getLoopsGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getInstructionsGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                //añadir mas inventarios a los que pueda acceder esta gui
                mainPlugin.getFunctionGUI().saveChanges(false);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar ReturnGUI
        }else if(event.getInventory().equals(mainPlugin.getReturnGUI().getGui())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //esta funcion guarda automaticamente las funciones si se cierra forzosamente el inventario
                mainPlugin.getReturnGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar set value gui
        }else if(event.getInventory().equals(mainPlugin.getSetValueGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getVariableGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getReturnGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getTextGUI().getGUI()))return;
                if(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios cuando sea necesario
                //esta funcion guarda automaticamente las funciones si se cierra forzosamente el inventario
                mainPlugin.getSetValueGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar math gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getSetValueGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getNumberGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI()))return;
                if(PluginCoder.getCoderGUI().getParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getForParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().getMathGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar number gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getNumberGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI()))return;
                PluginCoder.getCoderGUI().getNumberGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar conditions gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getSetValueGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI()))return;
                if(PluginCoder.getCoderGUI().getParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getConditionsGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar checkObjectType gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                PluginCoder.getCoderGUI().getCheckObjectTypeGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar equality gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI()))return;
                if(player.getOpenInventory().getTopInventory().equals(PluginCoder.getCoderGUI().getTextGUI().getGUI()))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getEqualityGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar text gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getTextGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(player.getOpenInventory().getTopInventory().equals(mainPlugin.getExecutionWriterGUI().getGui()))return;
                if(PluginCoder.getCoderGUI().getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getEqualityGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getTextColorGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getTextGUI().isAddingText())return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getTextGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
        }//cerrar text color gui
        else if(event.getInventory().equals(PluginCoder.getCoderGUI().getTextColorGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getTextGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getTextGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar parameters gui
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getTextGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getMathGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getConditionsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getParametersGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar forParametersGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getForParametersGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getMathGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getForParametersGUI().isEditingVar())return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getForParametersGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar functionParametersGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getFunctionParametersGUI().isAddingParam())return;
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(mainPlugin.getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getFunctionParametersGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar constructorGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getParametersGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().stream().anyMatch(inv->inv.equals(player.getOpenInventory().getTopInventory())))return;
                if(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                //TODO añadir mas inventarios
                PluginCoder.getCoderGUI().getConstructorsGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar objectConstructorsGUI
        }else if(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().getConstructorsGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
            //cerrar listConstructorGUI
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().getListConstructorGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
        }else if(event.getInventory().equals(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI())){
            Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->{
                if(PluginCoder.getCoderGUI().getPluginCoderGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                if(PluginCoder.getCoderGUI().getSetValueGUI().getGUI().equals(player.getOpenInventory().getTopInventory()))return;
                PluginCoder.getCoderGUI().getDictConstructorGUI().saveChanges(true);
                PluginCoder.getCoderGUI().returnHome(player,false);
            }, 1);
        }
    }
}
