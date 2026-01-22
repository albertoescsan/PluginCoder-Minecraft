package berty.plugincoder.GUI.listener;

import org.bukkit.event.inventory.InventoryDragEvent;
import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.interpreter.Language;
import berty.plugincoder.interpreter.command.Command;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.compiler.PluginCompiler;
import berty.plugincoder.main.PluginCoder;
import berty.plugincoder.GUI.InicVars;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PluginGuiListener implements Listener {

	private PluginCoder mainPlugin;
	public PluginGuiListener(PluginCoder pluginCoder) {mainPlugin=pluginCoder;}

	@EventHandler
	public void alClickearInventarioGui(InventoryClickEvent event){
		if(!(event.getWhoClicked() instanceof Player))return;
		if((event.getCurrentItem()==null||event.getCurrentItem().getType()==Material.AIR)
		&&PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()<0&&PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()<0)return;
		Player player = (Player) event.getWhoClicked();
		if(event.getInventory().equals(mainPlugin.getCoderGUI().getPluginCoderGUI())){//inv principal
			InicVars.functionType="";
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getPluginsGUI().updateGUI();
				player.openInventory(PluginCoder.getCoderGUI().getPluginsGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				player.openInventory(mainPlugin.getCoderGUI().getLanguagesGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			} else if(event.getSlot()==20) {
				//comandos
				player.openInventory(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==22) {
				//objetos
				player.openInventory(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==24) {
				//eventos
				player.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==38) {
				//enable
				PluginCoder.getCoderGUI().getFunctionGUI().setPreviousInventory(event.getInventory());
				String onEnableFunction=mainPlugin.getSelectedPlugin().getActivationContainer().get(0);
				PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(onEnableFunction,mainPlugin.getSelectedPlugin().getActivationContainer());
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==40) {
				//plugin
				PluginCoder.getCoderGUI().getObjectGUI().updateGUI(mainPlugin.getSelectedPlugin().getMainObject());
				player.openInventory(PluginCoder.getCoderGUI().getObjectGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==42) {
				//disable
				PluginCoder.getCoderGUI().getFunctionGUI().setPreviousInventory(event.getInventory());
				String onDisableFunction=mainPlugin.getSelectedPlugin().getDeactivationContainer().get(0);
				PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(onDisableFunction,mainPlugin.getSelectedPlugin().getDeactivationContainer());
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==4){
				PluginCoder.getCoderGUI().buttonSound(player);
				Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () -> {
					String exportedMessage="";
					try{
						PluginCompiler.compilePlugin(mainPlugin.getSelectedPlugin());
						exportedMessage=ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("exportedPlugin")
								.replace("%plugin%",ChatColor.GOLD+mainPlugin.getSelectedPlugin().getName()+ChatColor.YELLOW);
					}catch (Exception e){
						e.printStackTrace();
						exportedMessage=ChatColor.RED+ErrorManager.getErrorTranslation().get("exportingError")
								.replace("%plugin%",ChatColor.YELLOW+mainPlugin.getSelectedPlugin().getName()+ChatColor.RED);
						PluginCompiler.deletePluginFiles(mainPlugin.getSelectedPlugin());
					}
					player.sendMessage(exportedMessage);
				}, 1);
			}
			//language gui
		}else if(event.getInventory().equals(mainPlugin.getCoderGUI().getLanguagesGUI())){//idiomas
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==10) changeLanguage(player,Language.ENGLISH);
			else if(event.getSlot()==11) changeLanguage(player,Language.SPANISH);
			else if(event.getSlot()==12) changeLanguage(player,Language.PORTUGUESE);
			else if(event.getSlot()==13) changeLanguage(player,Language.FRENCH);
			else if(event.getSlot()==14) changeLanguage(player,Language.GERMAN);
			else if(event.getSlot()==15) changeLanguage(player,Language.ITALIAN);
			else if(event.getSlot()==16) changeLanguage(player,Language.RUSSIAN);
			//plugins gui
		}else if(mainPlugin.getCoderGUI().getPluginsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			int index=PluginCoder.getCoderGUI().getPluginsGUI().getGUI().indexOf(event.getInventory())*28+event.getSlot()-
					(10+2*(event.getSlot()/9-1));
			if(event.getCurrentItem()!=null&&!(event.getSlot()%9==0||(event.getSlot()+1)%9==0||(event.getSlot()>0&&event.getSlot()<8)||
					(event.getSlot()>45&&event.getSlot()<53))){
				Plugin selectedPlugin=mainPlugin.getPlugins().get(index);
				if(!PluginCoder.getCoderGUI().getPluginsGUI().isDeletePlugin()){
					PluginCoder.getCoderGUI().getPluginsGUI().setPluginSelectedIndex(index);
					PluginCoder.getCoderGUI().getPluginsGUI().updatePluginEditor(selectedPlugin);
					player.openInventory(PluginCoder.getCoderGUI().getPluginsGUI().getPluginEditor());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else{
					if(selectedPlugin.equals(mainPlugin.getSelectedPlugin())){
						//TODO mensaje Plugin seleccionado no puede ser eliminado
						return;
					}
					Plugin pluginDeleted=mainPlugin.getPlugins().remove(index);
					File pluginFile = new File(mainPlugin.getDataFolder().getParentFile().getPath()+"/PluginCoder/plugins/"+pluginDeleted.getName()+".txt");
					if (pluginFile.exists())pluginFile.delete();
					event.getInventory().setItem(event.getSlot(),null);
					PluginCoder.getCoderGUI().updateInventoriesContent(mainPlugin.getCoderGUI().getPluginsGUI().getGUI());
					if(!mainPlugin.getCoderGUI().getPluginsGUI().getGUI().contains(event.getInventory())){
						player.openInventory(mainPlugin.getCoderGUI().getPluginsGUI().getGUI().get(mainPlugin.getCoderGUI().getPluginsGUI().getGUI().size()-1));
					}else player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else{
				if(event.getSlot()==8||event.getSlot()==0){
					player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
					mainPlugin.getCoderGUI().getPluginsGUI().setDeletePlugin(false);
					ItemStack deletePlugin=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deletePlugin.setType(Material.LAVA_BUCKET);
					else deletePlugin.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getPluginsGUI().getGUI()){
						inventory.setItem(35,deletePlugin);
					}
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				else if(event.getSlot()==45||event.getSlot()==53){
					List<Inventory> inventories=PluginCoder.getCoderGUI().getPluginsGUI().getGUI();
					int invIndex=inventories.indexOf(event.getInventory());
					if(event.getSlot()==45){
						if(invIndex!=0){
							player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
						}
					}else if(invIndex<inventories.size()-1){
						player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if(event.getSlot()==26){
					mainPlugin.getCoderGUI().getPluginsGUI().setLastOpenedPage(index/28);
					mainPlugin.getCoderGUI().getPluginsGUI().setPlayerToEditPluginName(player);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==35){
					PluginCoder.getCoderGUI().getPluginsGUI().setDeletePlugin(!PluginCoder.getCoderGUI().getPluginsGUI().isDeletePlugin());
					ItemStack deletePlugin=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deletePlugin.setType(Material.LAVA_BUCKET);
					else deletePlugin.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getPluginsGUI().getGUI()){
						inventory.setItem(35,deletePlugin);
					}
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}
			//plugin editor gui
		}else if(mainPlugin.getCoderGUI().getPluginsGUI().getPluginEditor().equals(event.getInventory())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			Plugin selectedPlugin=mainPlugin.getPlugins().get(mainPlugin.getCoderGUI().getPluginsGUI().getPluginSelectedIndex());
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getPluginsGUI().updateGUI();
				player.openInventory(PluginCoder.getCoderGUI().getPluginsGUI().getGUI()
						.get(PluginCoder.getCoderGUI().getPluginsGUI().getPluginSelectedIndex()/28));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			} else if(event.getSlot()==22){//seleccionar plugin
				if(mainPlugin.getSelectedPlugin().getName().equals(selectedPlugin.getName()))return;
				//actualizar los tipos del CheckObjectType gui
				for(PluginObject object:mainPlugin.getSelectedPlugin().getObjects()){
					PluginCoder.getCoderGUI().getCheckObjectTypeGUI().removeObjectToTypeList(object.getName());
				}
				for(PluginObject object:selectedPlugin.getObjects()){
					PluginCoder.getCoderGUI().getCheckObjectTypeGUI().addObjectToTypeList(object.getName());
				}
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().sortObjectTypes();
				mainPlugin.setSelectedPlugin(selectedPlugin);PluginCoder.getCoderGUI().updateInventories();
				PluginCoder.getCoderGUI().getObjectConstructorsGUI().updateGUI(); //actualizar constructores de objeto
				PluginCoder.getCoderGUI().updatePluginItem(selectedPlugin.getName());
				player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==38){//editar nombre plugin
				mainPlugin.getCoderGUI().getPluginsGUI().setPlayerToEditPluginName(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==42){//activar/desactivar plugin
				selectedPlugin.setEnabled(!selectedPlugin.isEnabled());
				ItemStack enable=mainPlugin.getCoderGUI().getPluginsGUI().getActivationItem(selectedPlugin);
				event.getInventory().setItem(42,enable);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		}
		//commands gui
		else if(PluginCoder.getCoderGUI().getCommandsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			int commmandIndex=PluginCoder.getCoderGUI().getCommandsGUI().getGUI().indexOf(event.getInventory())*4+(event.getSlot()/9)-1;
			if(event.getSlot()==0||event.getSlot()==8){
				player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
				PluginCoder.getCoderGUI().getCommandsGUI().setDeleteCommand(false);
				ItemStack deleteCommand=event.getCurrentItem().clone();
				if(event.getCurrentItem().getType()==Material.BUCKET)deleteCommand.setType(Material.LAVA_BUCKET);
				else deleteCommand.setType(Material.BUCKET);
				for(Inventory inventory:PluginCoder.getCoderGUI().getCommandsGUI().getGUI()){
					inventory.setItem(35,deleteCommand);
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==45||event.getSlot()==53){
				List<Inventory> inventories=PluginCoder.getCoderGUI().getCommandsGUI().getGUI();
				int invIndex=inventories.indexOf(event.getInventory());
				if(event.getSlot()==45){
					if(invIndex!=0){
						player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if(invIndex<inventories.size()-1){
					player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getCurrentItem().getType().toString().equals("BOOK_AND_QUILL")||event.getCurrentItem().getType().toString().equals("WRITABLE_BOOK")){
				if(PluginCoder.getCoderGUI().getCommandsGUI().isDeleteCommand())return;
				PluginCoder.getCoderGUI().getFunctionGUI().setPreviousInventory(event.getInventory());
				PluginCoder.getCoderGUI().getCommandsGUI().updateCommandEdtitedIndexes(event.getInventory(),event.getSlot());
				PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(0,mainPlugin.getSelectedPlugin().getCommands().get(commmandIndex).getFunctionContainer());
				InicVars.functionType="command";
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==26){//añadir comando
				PluginCoder.getCoderGUI().getCommandPromptGUI().updateGUI(null);
				player.openInventory(PluginCoder.getCoderGUI().getCommandPromptGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==35){//activar/desactivar eliminar comando
				PluginCoder.getCoderGUI().getCommandsGUI().setDeleteCommand(!PluginCoder.getCoderGUI().getCommandsGUI().isDeleteCommand());
				ItemStack deleteCommand=event.getCurrentItem().clone();
				if(event.getCurrentItem().getType()==Material.BUCKET)deleteCommand.setType(Material.LAVA_BUCKET);
				else deleteCommand.setType(Material.BUCKET);
				for(Inventory inventory:PluginCoder.getCoderGUI().getCommandsGUI().getGUI()){
					inventory.setItem(35,deleteCommand);
				}
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()/9>0&&event.getSlot()/9<5){
				if(event.getSlot()==2+9*(event.getSlot()/9)){//editar prompt
					if(PluginCoder.getCoderGUI().getCommandsGUI().isDeleteCommand())return;
					Command command=PluginCoder.getCoderGUI().getCommandsGUI().updateCommandEdtitedIndexes(event.getInventory(),event.getSlot());
					PluginCoder.getCoderGUI().getCommandPromptGUI().updateGUI(command);
					player.openInventory(PluginCoder.getCoderGUI().getCommandPromptGUI().getGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==4+9*(event.getSlot()/9)){//eliminar comando
					if(!PluginCoder.getCoderGUI().getCommandsGUI().isDeleteCommand())return;
					PluginCoder.getCoderGUI().getCommandsGUI().removeCommand(player,event.getInventory(),event.getSlot());
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}
		}//command prompt gui
		else if(PluginCoder.getCoderGUI().getCommandPromptGUI().getGUI().equals(event.getInventory())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			Command command=PluginCoder.getCoderGUI().getCommandPromptGUI().getCommand();
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getCommandPromptGUI().saveChanges(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getCommandPromptGUI().saveChanges(player);
				player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			if(command.getPrompt().split(" ").length==7)return;
			if(event.getSlot()==21){
				PluginCoder.getCoderGUI().getCommandPromptGUI().addNewArgument(CommandVarType.NONE, player);
			}else if(event.getSlot()==23){
				String prompt=command.getPrompt();
				if(prompt.isEmpty())return;
				String newPrompt="";
				String[] args=prompt.split(" ");
				if(args.length==1){
					command.setPrompt(newPrompt);
				}else{
					for(int i=0;i<args.length;i++){
						if(i!=args.length-1)newPrompt+=args[i]+" ";
					}
					newPrompt=newPrompt.substring(0,newPrompt.length()-1);
					command.setPrompt(newPrompt);
				}
				PluginCoder.getCoderGUI().getCommandPromptGUI().renderBar();
				PluginCoder.getCoderGUI().buttonSound(player);
				player.updateInventory();
			}else if(event.getSlot()==28){
				PluginCoder.getCoderGUI().getCommandPromptGUI().addNewArgument(CommandVarType.PLAYER, player);
			}else if(event.getSlot()==30){
				PluginCoder.getCoderGUI().getCommandPromptGUI().addNewArgument(CommandVarType.TEXT, player);
			}else if(event.getSlot()==32){
				PluginCoder.getCoderGUI().getCommandPromptGUI().addNewArgument(CommandVarType.NUMBER, player);
			}else if(event.getSlot()==34){
				PluginCoder.getCoderGUI().getCommandPromptGUI().addNewArgument(CommandVarType.BOOLEAN, player);
			}
		}
		//objetos gui
		else if(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			int index=PluginCoder.getCoderGUI().getObjectsGUI().getGUI().indexOf(event.getInventory())*28+event.getSlot()-
					(10+2*(event.getSlot()/9-1));
			if(event.getCurrentItem()!=null&&!(event.getSlot()%9==0||(event.getSlot()+1)%9==0||(event.getSlot()>0&&event.getSlot()<8)||
					(event.getSlot()>45&&event.getSlot()<53))){
				if(!PluginCoder.getCoderGUI().getObjectsGUI().isDeleteObject()){//abrir menú del objeto
					PluginCoder.getCoderGUI().getObjectGUI().updateGUI(mainPlugin.getSelectedPlugin().getObjects().get(index));
					PluginCoder.getCoderGUI().getObjectsGUI().setLastPageOpened(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().indexOf(event.getInventory()));
					player.openInventory(PluginCoder.getCoderGUI().getObjectGUI().getGUI());
				}else{
					mainPlugin.getSelectedPlugin().getObjects().remove(index);
					PluginCoder.getCoderGUI().getObjectConstructorsGUI().updateGUI(); //actualizar constructores de objeto
					event.getInventory().setItem(event.getSlot(),null);
					PluginCoder.getCoderGUI().updateInventoriesContent(PluginCoder.getCoderGUI().getObjectsGUI().getGUI());
					if(!PluginCoder.getCoderGUI().getObjectsGUI().getGUI().contains(event.getInventory())){
						player.openInventory(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().get(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().size()-1));
					}else player.updateInventory();
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else{
				if(event.getSlot()==8||event.getSlot()==0){
					PluginCoder.getCoderGUI().getObjectsGUI().setDeleteObject(false);
					ItemStack deleteObject=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deleteObject.setType(Material.LAVA_BUCKET);
					else deleteObject.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getObjectsGUI().getGUI()){
						inventory.setItem(35,deleteObject);
					}
					player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				else if(event.getSlot()==45||event.getSlot()==53){
					List<Inventory> inventories=PluginCoder.getCoderGUI().getObjectsGUI().getGUI();
					int invIndex=inventories.indexOf(event.getInventory());
					if(event.getSlot()==45){
						if(invIndex!=0){
							player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
						}
					}else if(invIndex<inventories.size()-1){
						player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if(event.getSlot()==26){//añadir objeto
					PluginCoder.getCoderGUI().getObjectsGUI().setToWriteObjectName(player);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==35){//eliminar objeto
					PluginCoder.getCoderGUI().getObjectsGUI().setDeleteObject(!PluginCoder.getCoderGUI().getObjectsGUI().isDeleteObject());
					ItemStack deleteObject=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deleteObject.setType(Material.LAVA_BUCKET);
					else deleteObject.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getObjectsGUI().getGUI()){
						inventory.setItem(35,deleteObject);
					}
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}
		}//objeto gui
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getObjectGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				if(!PluginCoder.getCoderGUI().getObjectGUI().getObject().getName().equals("Plugin")){
					player.openInventory(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().get(PluginCoder.getCoderGUI().getObjectsGUI().getLastPageOpened()));
				}
				else player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
				PluginCoder.getCoderGUI().getExecutionWriterGUI().setLastFunctionInstructionVars(-1);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==11){
				if(PluginCoder.getCoderGUI().getObjectGUI().displayProperties()){
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getSlot()==13){
				if(PluginCoder.getCoderGUI().getObjectGUI().displayFunctions()){
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);}
			}else if(event.getSlot()==15){
				if(PluginCoder.getCoderGUI().getObjectGUI().displayConstructors()){
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);}
			}else if(event.getSlot()==45){
				if(!PluginCoder.getCoderGUI().getObjectGUI().previousPage())return;
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==53){
				if(!PluginCoder.getCoderGUI().getObjectGUI().nextPage())return;
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>27&&event.getSlot()<35||event.getSlot()>36&&event.getSlot()<43){
				if(PluginCoder.getCoderGUI().getObjectGUI().isDeletingItem()){
					PluginCoder.getCoderGUI().getObjectGUI().deleteItem(event.getSlot());
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
					return;
				}
				if(event.getCurrentItem().getType().toString().equals("MAP")||event.getCurrentItem().getType()==Material.FILLED_MAP){
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					if(event.getCurrentItem().getItemMeta().getLore()==null)instruction+="=null";
					else instruction+="="+ChatColor.stripColor(event.getCurrentItem().getItemMeta().getLore().get(0)).trim();
					PluginCoder.getCoderGUI().getVariableGUI().updateGUI(instruction,event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getVariableGUI().getGui());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else{
					PluginObject object=PluginCoder.getCoderGUI().getObjectGUI().getObject();
					int renderedSlot=PluginCoder.getCoderGUI().getObjectGUI().getRenderedSlot();
					int renderedPage=PluginCoder.getCoderGUI().getObjectGUI().getRenderedPage();
					int index=(event.getSlot()<35?event.getSlot()-28:event.getSlot()-30)+renderedPage*14;
					PluginCoder.getCoderGUI().getFunctionGUI().setPreviousInventory(event.getInventory());
					PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(index,renderedSlot==13?
							object.getFunctions():object.getConstructors());
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				PluginCoder.getCoderGUI().getObjectGUI().updateSelectedIndex(event.getSlot());
			}else if(event.getSlot()==48){
				PluginCoder.getCoderGUI().getObjectGUI().addItem(player);
				PluginCoder.getCoderGUI().buttonSound(player);
				player.updateInventory();
			}else if(event.getSlot()==50){
				ItemStack deleteObject=event.getCurrentItem();
				if(event.getCurrentItem().getType()==Material.BUCKET)deleteObject.setType(Material.LAVA_BUCKET);
				else deleteObject.setType(Material.BUCKET);
				PluginCoder.getCoderGUI().getObjectGUI().setDeletingItem(!PluginCoder.getCoderGUI().getObjectGUI().isDeletingItem());
				PluginCoder.getCoderGUI().buttonSound(player);
				player.updateInventory();
			}
		}//eventos gui
		else if(PluginCoder.getCoderGUI().getEventsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(!(event.getSlot()%9==0||(event.getSlot()+1)%9==0||(event.getSlot()>0&&event.getSlot()<8)||
					(event.getSlot()>45&&event.getSlot()<53))){
				int index=PluginCoder.getCoderGUI().getEventsGUI().getGUI().indexOf(event.getInventory())*28+event.getSlot()-
						(10+2*(event.getSlot()/9-1));
				if(!PluginCoder.getCoderGUI().getEventsGUI().isDeleteEvent()){
					PluginCoder.getCoderGUI().getFunctionGUI().setPreviousInventory(event.getInventory());
					PluginCoder.getCoderGUI().getEventsGUI().setEventEditedIndex(event.getInventory(),event.getSlot());
					PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(index,mainPlugin.getSelectedPlugin().getListener());
					InicVars.functionType="event";
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
				}else{
					mainPlugin.getSelectedPlugin().getListener().remove(index);
					event.getInventory().setItem(event.getSlot(),null);
					PluginCoder.getCoderGUI().updateInventoriesContent(PluginCoder.getCoderGUI().getEventsGUI().getGUI());
					if(!PluginCoder.getCoderGUI().getEventsGUI().getGUI().contains(event.getInventory())){
						player.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getGUI().get(PluginCoder.getCoderGUI().getEventsGUI().getGUI().size()-1));
					}
					else player.updateInventory();
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else{
				if(event.getSlot()==8||event.getSlot()==0){
					player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
					PluginCoder.getCoderGUI().getEventsGUI().setDeleteEvent(false);
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				else if(event.getSlot()==45||event.getSlot()==53){
					List<Inventory> inventories=PluginCoder.getCoderGUI().getEventsGUI().getGUI();
					int invIndex=inventories.indexOf(event.getInventory());
					if(event.getSlot()==45){
						if(invIndex!=0){
							player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
						}
					}else if(invIndex<inventories.size()-1){
						player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if(event.getSlot()==26){//añadir evento
					PluginCoder.getCoderGUI().getEventsGUI().setLastPageOpened(PluginCoder.getCoderGUI().getEventsGUI().getGUI().indexOf(event.getInventory()));
					player.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getEventsItemsGUI().get(0));
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==35){//eliminar evento
					PluginCoder.getCoderGUI().getEventsGUI().setDeleteEvent(!PluginCoder.getCoderGUI().getEventsGUI().isDeleteEvent());
					ItemStack deleteEvent=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deleteEvent.setType(Material.LAVA_BUCKET);
					else deleteEvent.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getEventsGUI().getGUI()){
						inventory.setItem(35,deleteEvent);
					}
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}
			//funciones gui
		}else if(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory()==null)return;
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(PluginCoder.getCoderGUI().getFunctionGUI().isRunningCode()&&event.getSlot()!=49)return;
			int inventoryIndex=PluginCoder.getCoderGUI().getFunctionGUI().getGUI().indexOf(event.getInventory());
			if(event.getSlot()%9==0||(event.getSlot()+1)%9==0||(event.getSlot()>0&&event.getSlot()<8)||
					(event.getSlot()>45&&event.getSlot()<53)){
				if(event.getSlot()==8){
					ItemStack cursor=player.getItemOnCursor().clone();
					PluginCoder.getCoderGUI().getFunctionGUI().restoreInstructionMovement(player);
					PluginCoder.getCoderGUI().getFunctionGUI().returnHome(player,true);
					player.getInventory().remove(cursor);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==0){
					PluginCoder.getCoderGUI().getFunctionGUI().setDeleteInstruction(false);
					ItemStack delete=event.getInventory().getItem(35).clone();
					delete.setType(Material.BUCKET);event.getInventory().setItem(35,delete);
					PluginCoder.getCoderGUI().getFunctionGUI().returnPage(player);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==4&&!event.getCurrentItem().getType().toString().contains("GLASS_PANE")){
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					instruction=instruction.replaceAll("^([^{]+)\\{(.+)$","$1");
					if(instruction.matches("^for\\s*\\((.*)$")){
						PluginCoder.getCoderGUI().getForParametersGUI().updateGUI(instruction);
						player.openInventory(PluginCoder.getCoderGUI().getForParametersGUI().getGUI());
					}else if(mainPlugin.getSelectedPlugin().getMainObject().getFunctions()==
							PluginCoder.getCoderGUI().getFunctionGUI().getInitialFunctionContainer()||
							mainPlugin.getSelectedPlugin().getObjects().stream().anyMatch(obj->obj.getFunctions()==
							PluginCoder.getCoderGUI().getFunctionGUI().getInitialFunctionContainer()||
									obj.getConstructors()==PluginCoder.getCoderGUI().getFunctionGUI().getInitialFunctionContainer())){
						PluginCoder.getCoderGUI().getFunctionParametersGUI().updateInventory(instruction);
						player.openInventory(PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI());
					}else{
						PluginCoder.getCoderGUI().getParametersGUI().updateInventory(instruction,"", event.getClickedInventory());
						player.openInventory(PluginCoder.getCoderGUI().getParametersGUI().getGUI());
					}
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==45||event.getSlot()==53){
					List<Inventory> inventories=PluginCoder.getCoderGUI().getFunctionGUI().getGUI();
					int invIndex=inventories.indexOf(event.getInventory());
					if(event.getSlot()==45){
						if(invIndex!=0){
							player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
						}
					}else if(invIndex<inventories.size()-1){
						player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if(event.getSlot()==26){//añadir instruccion
					PluginCoder.getCoderGUI().getFunctionGUI().setLastPageOpened(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().indexOf(event.getInventory()));
					player.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getGui());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==35){//eliminar instruccion
					if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>-2||PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()>-2)return;
					PluginCoder.getCoderGUI().getFunctionGUI().setDeleteInstruction(!PluginCoder.getCoderGUI().getFunctionGUI().isDeleteInstruction());
					ItemStack deleteInstruction=event.getCurrentItem().clone();
					if(event.getCurrentItem().getType()==Material.BUCKET)deleteInstruction.setType(Material.LAVA_BUCKET);
					else deleteInstruction.setType(Material.BUCKET);
					for(Inventory inventory:PluginCoder.getCoderGUI().getFunctionGUI().getGUI()){
						inventory.setItem(35,deleteInstruction);
					}
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==18){//mover instruccion
					if(PluginCoder.getCoderGUI().getFunctionGUI().isDeleteInstruction()||PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()>-2)return;
					if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()!=-2){
						if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>-1){
							int instructionSlot=PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()%28;
							instructionSlot=instructionSlot+(10+2*(instructionSlot/7));
							PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionItem(player.getItemOnCursor().clone(),inventoryIndex,instructionSlot);
							player.updateInventory();player.setItemOnCursor(null);
						}
						PluginCoder.getCoderGUI().getFunctionGUI().setMoveInstructionIndex(-2);
					}else PluginCoder.getCoderGUI().getFunctionGUI().setMoveInstructionIndex(-1);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==27){//copiar instruccion
					if(PluginCoder.getCoderGUI().getFunctionGUI().isDeleteInstruction()||PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>-2)return;
					if(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()!=-2){
						PluginCoder.getCoderGUI().getFunctionGUI().setCopyInstructionIndex(-2);
						player.setItemOnCursor(null);
					}else PluginCoder.getCoderGUI().getFunctionGUI().setCopyInstructionIndex(-1);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==49){
					if(event.getCurrentItem().getType().toString().contains("GLASS_PANE"))return;
					if(!event.getCurrentItem().getType().toString().contains("SKULL_ITEM")&&!event.getCurrentItem().getType().toString().contains("PLAYER_HEAD")){
						String lastInstruction=PluginCoder.getCoderGUI().getFunctionGUI().getLastInstruction();
						if(lastInstruction.equals("cancel")||lastInstruction.equals("continue")||lastInstruction.equals("stop"))return;
						PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("cancel");
						PluginCoder.getCoderGUI().buttonSound(player);
						player.updateInventory();
					}else{ //ejecutar funcion
						if(!PluginCoder.getCoderGUI().getFunctionGUI().isRunningCode())PluginCoder.getCoderGUI().getFunctionGUI().executeCode(player);
						else PluginCoder.getCoderGUI().getFunctionGUI().stopCodeExecution();
						player.updateInventory();
						PluginCoder.getCoderGUI().buttonSound(player);
					}
				}
				//añadir instrucciones exclusivas de funcion
				else if((event.getSlot()==47||event.getSlot()==51)&&!event.getCurrentItem().getType().toString().contains("GLASS_PANE")){
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					String lastInstruction=PluginCoder.getCoderGUI().getFunctionGUI().getLastInstruction();
					if((instruction.equalsIgnoreCase("continue")||instruction.equalsIgnoreCase("stop"))&&
							(lastInstruction.equalsIgnoreCase("continue")||lastInstruction.equalsIgnoreCase("stop")))return;
					PluginCoder.getCoderGUI().getFunctionGUI().addInstruction(instruction);
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else{
				int instructionIndex=inventoryIndex*28+event.getSlot()-(10+2*(event.getSlot()/9-1));
				if(PluginCoder.getCoderGUI().getFunctionGUI().isDeleteInstruction()){//borrar instruccion
					PluginCoder.getCoderGUI().getFunctionGUI().deleteInstruction(instructionIndex);
					event.getInventory().setItem(event.getSlot(),null);
					PluginCoder.getCoderGUI().updateInventoriesContent(PluginCoder.getCoderGUI().getFunctionGUI().getGUI());
					if(!PluginCoder.getCoderGUI().getFunctionGUI().getGUI().contains(event.getInventory())){
						player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
					}else player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>-2){//mover instruccion
					if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()==-1){
						PluginCoder.getCoderGUI().getFunctionGUI().setMoveInstructionIndex(instructionIndex);
						player.setItemOnCursor(event.getCurrentItem().clone());
						event.getInventory().setItem(event.getSlot(),null);
						PluginCoder.getCoderGUI().updateInventoriesContent(PluginCoder.getCoderGUI().getFunctionGUI().getGUI());
						if(!PluginCoder.getCoderGUI().getFunctionGUI().getGUI().contains(event.getInventory())){
							player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
						}else player.updateInventory();
					}else{
						PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionInFunction(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex(),instructionIndex,true);
						PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionItem(player.getItemOnCursor().clone(),inventoryIndex,event.getSlot());
						player.setItemOnCursor(null);player.updateInventory();
						PluginCoder.getCoderGUI().getFunctionGUI().setMoveInstructionIndex(-2);
					}
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()>-2){//copiar instruccion
					if(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()==-1){
						PluginCoder.getCoderGUI().getFunctionGUI().setCopyInstructionIndex(instructionIndex);
						player.setItemOnCursor(event.getCurrentItem().clone());
					}else{
						PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionInFunction(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex(),instructionIndex,false);
						PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionItem(player.getItemOnCursor().clone(),inventoryIndex,event.getSlot());
						player.setItemOnCursor(null);player.updateInventory();
						PluginCoder.getCoderGUI().getFunctionGUI().setCopyInstructionIndex(-2);
					}
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				else {
					PluginCoder.getCoderGUI().getFunctionGUI().updateFunctionIndexes(event.getInventory(),event.getSlot());
					if(event.getCurrentItem().getType().equals(mainPlugin.getCodeUtils().getVersionedMaterial(Material.WRITABLE_BOOK))){
						PluginCoder.getCoderGUI().getFunctionGUI().saveChanges(false);
						PluginCoder.getCoderGUI().getFunctionGUI().updateGUIWithNewFunction(instructionIndex,PluginCoder.getCoderGUI().getFunctionGUI().getInitialFunctionContainer());
						player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(0));
					}else{
						PluginCoder.getCoderGUI().getFunctionGUI().setLastPageOpened(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().indexOf(event.getInventory()));
						String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
						if(instruction.matches("^return\\s+(.*)$")){
							PluginCoder.getCoderGUI().getReturnGUI().updateGUI(instruction);
							player.openInventory(PluginCoder.getCoderGUI().getReturnGUI().getGui());
						}
						else if(instruction.matches("^(.+)\\=(.*)$")){
							PluginCoder.getCoderGUI().getVariableGUI().updateGUI(instruction,event.getClickedInventory());
							player.openInventory(PluginCoder.getCoderGUI().getVariableGUI().getGui());
						}
						else {
							PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction,event.getClickedInventory());
							player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
						}
					}
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}
			//añadir evento gui
		}else if(PluginCoder.getCoderGUI().getEventsGUI().getEventsItemsGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()%9==0||(event.getSlot()+1)%9==0||(event.getSlot()>0&&event.getSlot()<8)||
					(event.getSlot()>45&&event.getSlot()<53)){
				if(event.getSlot()==0){
					player.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getGUI().get(PluginCoder.getCoderGUI().getEventsGUI().getLastPageOpened()));
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==8){
					player.openInventory(PluginCoder.getCoderGUI().getPluginCoderGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else if(event.getSlot()==45||event.getSlot()==53){
					List<Inventory> inventories=PluginCoder.getCoderGUI().getEventsGUI().getEventsItemsGUI();
					int invIndex=inventories.indexOf(event.getInventory());
					if(event.getSlot()==45){
						if(invIndex!=0){
							player.openInventory(inventories.get(invIndex-1));PluginCoder.getCoderGUI().buttonSound(player);
						}
					}else if(invIndex<inventories.size()-1){
						player.openInventory(inventories.get(invIndex+1));PluginCoder.getCoderGUI().buttonSound(player);
					}
				}
			}else if(event.getCurrentItem()!=null){
				String eventName=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				String newEvent=eventName+"{}";
				mainPlugin.getSelectedPlugin().getListener().add(newEvent);
				PluginCoder.getCoderGUI().getEventsGUI().updateGUI();
				PluginCoder.getCoderGUI().buttonSound(player);
				player.openInventory(PluginCoder.getCoderGUI().getEventsGUI().getGUI().get(PluginCoder.getCoderGUI().getEventsGUI().getLastPageOpened()));
			}
			//añadir instruccion gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getLastPageOpened()));
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==10){
				PluginCoder.getCoderGUI().getVariableGUI().setNewVar(true);
				PluginCoder.getCoderGUI().getVariableGUI().setPlayerToEditVarName(player);
				mainPlugin.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==11){
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("")){
					PluginCoder.getCoderGUI().buttonSound(player);
					Inventory inventory=PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1);
					int functionIndex=PluginCoder.getCoderGUI().getFunctionGUI().getNumOfInstructions()-1;
					PluginCoder.getCoderGUI().getFunctionGUI().getFunctionsIndexes().add(functionIndex);
					PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI("",inventory);
					player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				}
			}else if(event.getSlot()==12){
				PluginCoder.getCoderGUI().buttonSound(player);
				player.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getConditionalsGui());
			}
			else if(event.getSlot()==13){
				PluginCoder.getCoderGUI().buttonSound(player);
				player.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getLoopsGui());
			}else if(event.getSlot()==14){
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("delay(){}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}
			else if(event.getSlot()==15){
				if(PluginCoder.getCoderGUI().getFunctionGUI().getFunctions().stream().anyMatch(f->f.matches("^repeat\\((.*)\\)\\{(.*)\\}$"))){
					ErrorManager.setSender(player);
					ErrorManager.repeatInsideOfRepeat("");
					ErrorManager.setSender(Bukkit.getConsoleSender());
					PluginCoder.getCoderGUI().errorSound(player);
					return;
				}
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("repeat(){}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}else if(event.getSlot()==16){
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("return ")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getConditionalsGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				player.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if (event.getSlot()==11) {
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("if(){}"))PluginCoder.getCoderGUI().buttonSound(player);
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
			}else if (event.getSlot()==13) {
				String instruction=PluginCoder.getCoderGUI().getFunctionGUI().getLastInstruction();
				if(!instruction.startsWith("if(")&&!instruction.startsWith("else if(")){
					ErrorManager.setSender(player);
					ErrorManager.elseWithoutIf("else if");
					PluginCoder.getCoderGUI().errorSound(player);
					ErrorManager.setSender(Bukkit.getConsoleSender());
					return;
				}
				PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("else if(){}");
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("else if(){}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}else if (event.getSlot()==15) {
				String instruction=PluginCoder.getCoderGUI().getFunctionGUI().getLastInstruction();
				if(!instruction.startsWith("if(")&&!instruction.startsWith("else if(")){
					ErrorManager.setSender(player);
					ErrorManager.elseWithoutIf("");
					PluginCoder.getCoderGUI().errorSound(player);
					ErrorManager.setSender(Bukkit.getConsoleSender());
					return;
				}
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("else{}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getInstructionsGUI().getLoopsGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				player.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==12){
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("for(){}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}else if(event.getSlot()==14){
				if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction("while(){}")){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1));
				}
			}
		}
		//variable gui
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getVariableGUI().getGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getVariableGUI().saveChanges(false);
				PluginCoder.getCoderGUI().getVariableGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getVariableGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==20){
				PluginCoder.getCoderGUI().getVariableGUI().setPlayerToEditVarName(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==24){
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				PluginCoder.getCoderGUI().getSetValueGUI().updateGUI(instruction,event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		}//execution writter gui
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getCurrentItem()==null||event.getCurrentItem().getType()==Material.AIR)return;
			if(event.getSlot()==0){
				//retorna y guarda
				PluginCoder.getCoderGUI().getExecutionWriterGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getExecutionWriterGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==22){
				if(PluginCoder.getCoderGUI().getExecutionWriterGUI().deleteMethod()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}

			}else if(event.getSlot()==45){
				if(PluginCoder.getCoderGUI().getExecutionWriterGUI().previousPage()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==53){
				if(PluginCoder.getCoderGUI().getExecutionWriterGUI().nextPage()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else{
				if(event.getSlot()>=10&&event.getSlot()<=16){
					String method=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
					if(method.matches("^(.+)\\((.*)\\)$")){
						PluginCoder.getCoderGUI().getExecutionWriterGUI().prepareToParametersGUI(event.getSlot());
						String methodParentType=PluginCoder.getCoderGUI().getExecutionWriterGUI().getMethodExecutedTypes().get(event.getSlot()-11);
						PluginCoder.getCoderGUI().getParametersGUI().updateInventory(method,methodParentType, event.getClickedInventory());
						player.openInventory(PluginCoder.getCoderGUI().getParametersGUI().getGUI());
						PluginCoder.getCoderGUI().buttonSound(player);
					}
				}else if((event.getSlot()>=28&&event.getSlot()<=34)||(event.getSlot()>=37&&event.getSlot()<=43)){
					if(PluginCoder.getCoderGUI().getExecutionWriterGUI().selectNewMethod(event.getCurrentItem())){
						PluginCoder.getCoderGUI().buttonSound(player);
						player.updateInventory();
					}
				}
			}
		}//returnGUI
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getReturnGUI().getGui())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getReturnGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getLastPageOpened()));
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getReturnGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==22){
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());
				PluginCoder.getCoderGUI().getSetValueGUI().updateGUI(instruction,event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//setValueGUI
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getSetValueGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			String instruction=ChatColor.stripColor(event.getInventory().getItem(22).getItemMeta().getDisplayName()).trim();
			if(instruction.equals("null"))instruction="";
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getSetValueGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getSetValueGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==4){
				if(ChatColor.stripColor(event.getInventory().getItem(22).getItemMeta().getDisplayName()).trim().isEmpty())return;
				ItemStack instructionItem=new ItemStack(mainPlugin.getVersionNumber()<13?
						Material.getMaterial("EMPTY_MAP"):Material.MAP);
				ItemMeta meta=instructionItem.getItemMeta();
				meta.setDisplayName(ChatColor.YELLOW+"");
				instructionItem.setItemMeta(meta);
				event.getInventory().setItem(22,instructionItem);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==38){
				PluginCoder.getCoderGUI().getTextGUI().updateInventory(instruction,event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==39){
				//comprobar si se puede cargar la instruccion en el MathGUI
				if(instruction.isEmpty()||PluginCoder.getCoderGUI().getMathGUI().executionIsNumber(instruction)){
					PluginCoder.getCoderGUI().getMathGUI().updateGUI(instruction,event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.notNumberInstruction();
			}else if(event.getSlot()==40){
				if(instruction.isEmpty()||PluginCoder.getCoderGUI().getConditionsGUI().executionIsBoolean(instruction)){
					PluginCoder.getCoderGUI().getConditionsGUI().updateInventory(instruction,event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getConditionsGUI().getGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.notConditionalInstruction();
			}else if(event.getSlot()==41){
				//comprobar si se puede cargar la instruccion en el ExecutionWriterGUI
				if(instruction.isEmpty()||PluginCoder.getCoderGUI().getExecutionWriterGUI().getTypeOfExecution(instruction)!=null){
					PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction, event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.notExecutableSequence();
			}else if(event.getSlot()==42){//cargar o mostrar seleccion de constructores
				PluginCoder.getCoderGUI().getConstructorsGUI().updateInventory(event.getClickedInventory(),instruction);
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//mathGUI
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getMathGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getMathGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getMathGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
				//cambiar operacion
			}else if(event.getSlot()>9&&event.getSlot()<17&&event.getCurrentItem()!=null){
				if(event.getCurrentItem().getType().toString().equals("PLAYER_HEAD")||
						event.getCurrentItem().getType().toString().equals("SKULL_ITEM"))return;
				PluginCoder.getCoderGUI().getMathGUI().getLastIndex().add(event.getSlot()-10+PluginCoder.getCoderGUI().getMathGUI().getStartContentIndex());
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				PluginCoder.getCoderGUI().getMathGUI().getIsNewNumber().add(false);
				if(!instruction.matches("^\\d+(\\.\\d+)?$")){
					PluginCoder.getCoderGUI().getMathGUI().prepareToNextGUI();
					PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction, event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				}else{
					PluginCoder.getCoderGUI().getNumberGUI().updateGUI(instruction);
					player.openInventory(PluginCoder.getCoderGUI().getNumberGUI().getGUI());
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}//añadir operacion o parentesis
			else if(event.getSlot()>36&&event.getSlot()<44){
				String symbol=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				if(symbol.equals("x"))symbol="*";
				if(PluginCoder.getCoderGUI().getMathGUI().getMathContents().isEmpty()){
					if(symbol.equals("-")||symbol.equals("(")||symbol.equals("(")){
						PluginCoder.getCoderGUI().getMathGUI().addNewContent(symbol);
						PluginCoder.getCoderGUI().buttonSound(player);
					}
					return;
				}
				if(PluginCoder.getCoderGUI().getMathGUI().checkNewContent(symbol)){
					PluginCoder.getCoderGUI().getMathGUI().addNewContent(symbol);
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.consecutiveMathSymbols();
			}else if(event.getSlot()==30){
				if(!PluginCoder.getCoderGUI().getMathGUI().checkNewContent("instruction"))return;
				PluginCoder.getCoderGUI().getMathGUI().getIsNewNumber().add(true);
				PluginCoder.getCoderGUI().getMathGUI().prepareToNextGUI();
				PluginCoder.getCoderGUI().getNumberGUI().updateGUI("");
				player.openInventory(PluginCoder.getCoderGUI().getNumberGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==32){
				if(!PluginCoder.getCoderGUI().getMathGUI().checkNewContent("instruction"))return;
				PluginCoder.getCoderGUI().getMathGUI().getIsNewNumber().add(true);
				PluginCoder.getCoderGUI().getMathGUI().prepareToNextGUI();
				PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI("", event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==22){
				if(PluginCoder.getCoderGUI().getMathGUI().getMathContents().size()>0){
					int contentIndex=PluginCoder.getCoderGUI().getMathGUI().getMathContents().size()-7;
					PluginCoder.getCoderGUI().getMathGUI().getMathContents()
							.remove(PluginCoder.getCoderGUI().getMathGUI().getMathContents().size()-1);
					if(contentIndex==PluginCoder.getCoderGUI()
							.getMathGUI().getStartContentIndex()&&contentIndex!=0)PluginCoder.getCoderGUI().getMathGUI().previousContent();
					else PluginCoder.getCoderGUI().getMathGUI().updateMathBar();
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getSlot()==18){
				if(PluginCoder.getCoderGUI().getMathGUI().previousContent()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==26){
				if(PluginCoder.getCoderGUI().getMathGUI().nextContent()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}
			//Number GUI
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getNumberGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				player.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getNumberGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==39){//borrar ultimo
				String oldNumbers=ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getDisplayName()).trim();
				if(!oldNumbers.isEmpty()){
					PluginCoder.getCoderGUI().getNumberGUI().updateGUI(oldNumbers.substring(0,oldNumbers.length()-1));
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getSlot()==41){//guardar
				PluginCoder.getCoderGUI().getNumberGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getCurrentItem().getType().toString().equals("PLAYER_HEAD")||
					event.getCurrentItem().getType().toString().equals("SKULL_ITEM")){
				String newNumber=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				String oldNumbers=ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getDisplayName()).trim();
				if(newNumber.equals(".")&&oldNumbers.contains("."))return;
				PluginCoder.getCoderGUI().getNumberGUI().updateGUI(oldNumbers+newNumber);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//conditions GUI
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getConditionsGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getConditionsGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getConditionsGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>9&&event.getSlot()<17&&event.getCurrentItem()!=null){
				if(event.getCurrentItem().getType().toString().equals("PLAYER_HEAD")||
						event.getCurrentItem().getType().toString().equals("SKULL_ITEM"))return;
				PluginCoder.getCoderGUI().getConditionsGUI().getLastIndex().add(event.getSlot()-10+PluginCoder.getCoderGUI().getConditionsGUI().getStartContentIndex());
				PluginCoder.getCoderGUI().getConditionsGUI().getIsNewElement().add(false);
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				if(instruction.equals("true")||instruction.equals("false"))return;
				String booleanType=PluginCoder.getCoderGUI().getConditionsGUI().getBooleanType(instruction);
				if(booleanType.equals("checkObjectType")){
					//load check object type
					PluginCoder.getCoderGUI().getCheckObjectTypeGUI().updateInventory(instruction);
					player.openInventory(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI());
				}else if(booleanType.equals("equality")){
					//load equality gui
					PluginCoder.getCoderGUI().getEqualityGUI().updateGUI(instruction);
					player.openInventory(PluginCoder.getCoderGUI().getEqualityGUI().getGUI());
				}else{
					//load execution gui
					PluginCoder.getCoderGUI().getConditionsGUI().prepareToNextGUI();
					PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction, event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>36&&event.getSlot()<44){
				if(!PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().isEmpty()){
					String conditional=PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().get(0);
					if(conditional.equalsIgnoreCase("true")||conditional.equalsIgnoreCase("false"))return;
				}
				String symbol=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				//evitar que se use and u or al principio
				if((symbol.equals("and")||symbol.equals("or"))&&PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().isEmpty())return;
				if((symbol.equals("true")||symbol.equals("false"))&&!PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().isEmpty())return;
				if(PluginCoder.getCoderGUI().getConditionsGUI().checkNewContent(symbol)){
					PluginCoder.getCoderGUI().getConditionsGUI().addNewContent(symbol);
					PluginCoder.getCoderGUI().buttonSound(player);
				}
				else ErrorManager.consecutiveLogicSymbols();
			}else if(event.getSlot()==22){
				if(PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().size()>0){
					int contentIndex=PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().size()-7;
					PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents()
							.remove(PluginCoder.getCoderGUI().getConditionsGUI().getConditionalContents().size()-1);
					if(contentIndex==PluginCoder.getCoderGUI()
							.getConditionsGUI().getStartContentIndex()&&contentIndex!=0)PluginCoder.getCoderGUI().getConditionsGUI().previousContent();
					else PluginCoder.getCoderGUI().getConditionsGUI().updateConditionsBar();
					player.updateInventory();
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getSlot()==18){
				if(PluginCoder.getCoderGUI().getConditionsGUI().previousContent()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==26){
				if(PluginCoder.getCoderGUI().getConditionsGUI().nextContent()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==29){
				//equality
				if(!PluginCoder.getCoderGUI().getConditionsGUI().checkNewContent("instruction"))return;
				PluginCoder.getCoderGUI().getConditionsGUI().getIsNewElement().add(true);
				PluginCoder.getCoderGUI().getEqualityGUI().updateGUI("");
				player.openInventory(PluginCoder.getCoderGUI().getEqualityGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==31){
				//variables
				if(!PluginCoder.getCoderGUI().getConditionsGUI().checkNewContent("instruction"))return;
				PluginCoder.getCoderGUI().getConditionsGUI().getIsNewElement().add(true);
				PluginCoder.getCoderGUI().getConditionsGUI().prepareToNextGUI();
				PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI("", event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==33){
				//check object type
				if(!PluginCoder.getCoderGUI().getConditionsGUI().checkNewContent("instruction"))return;
				PluginCoder.getCoderGUI().getConditionsGUI().getIsNewElement().add(true);
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().updateInventory("");
				player.openInventory(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//check object type gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getConditionsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==10){
				//executionWriter gui
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().prepareToNextGUI();
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction,event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==36){
				if(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().previousObjectType())PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==44){
				if(PluginCoder.getCoderGUI().getCheckObjectTypeGUI().nextObjectType())PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>=28&&event.getSlot()<=34&&event.getCurrentItem()!=null&&event.getCurrentItem().getType()!=Material.AIR){
				String oldType=ChatColor.stripColor(event.getInventory().getItem(16).getItemMeta().getDisplayName()).trim();
				String newType=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				if(oldType.equals(newType))return;
				event.getInventory().setItem(16,event.getCurrentItem());
				PluginCoder.getCoderGUI().getCheckObjectTypeGUI().updateInstructionItem();
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//Equality gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getEqualityGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			String instruction=ChatColor.stripColor(event.getInventory()
					.getItem(PluginCoder.getCoderGUI().getEqualityGUI().getSelectedInstructionSlot()).getItemMeta().getDisplayName()).trim();
			if(instruction.equals("null"))instruction="";
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getEqualityGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getConditionsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getEqualityGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==11||event.getSlot()==15){
				if(PluginCoder.getCoderGUI().getEqualityGUI().getSelectedInstructionSlot()==event.getSlot())return;
				PluginCoder.getCoderGUI().getEqualityGUI().selectInstructionItem(event.getSlot());
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==29){
				PluginCoder.getCoderGUI().getEqualityGUI().prepareToNextGUI();
				PluginCoder.getCoderGUI().getTextGUI().updateInventory(instruction, event.getInventory());
				player.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==31){
				//math gui
				if(PluginCoder.getCoderGUI().getMathGUI().executionIsNumber(instruction)){
					PluginCoder.getCoderGUI().getEqualityGUI().prepareToNextGUI();
					PluginCoder.getCoderGUI().getMathGUI().updateGUI(instruction,event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.notNumberInstruction();
			}else if(event.getSlot()==33){
				//executionWritter gui
				if(instruction.isEmpty()||PluginCoder.getCoderGUI().getExecutionWriterGUI().getTypeOfExecution(instruction)!=null){
					PluginCoder.getCoderGUI().getEqualityGUI().prepareToNextGUI();
					PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction, event.getInventory());
					player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
					PluginCoder.getCoderGUI().buttonSound(player);
				}else ErrorManager.notExecutableSequence();
			}else if(event.getSlot()==39||event.getSlot()==40||event.getSlot()==41){
				String operator=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				String previousOperator=ChatColor.stripColor(event.getInventory().getItem(13).getItemMeta().getDisplayName()).trim();
				ItemStack subOperator=event.getInventory().getItem(12);
				if(!(subOperator.getType().toString().equals("PLAYER_HEAD")||subOperator.getType().toString().equals("SKULL_ITEM"))&&operator.equals(previousOperator))return;
				if(event.getSlot()==40&&event.isShiftClick()){
					event.getInventory().setItem(12,PluginCoder.getCoderGUI().getEqualityGUI().getOperatorItem(previousOperator));
				}else {
					ItemStack negro=mainPlugin.getVersionNumber()<13?
							new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"),1,(short)15):new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
					ItemMeta meta=negro.getItemMeta();
					meta.setDisplayName(ChatColor.WHITE+"");
					negro.setItemMeta(meta);event.getInventory().setItem(12,negro);
				}
				event.getInventory().setItem(13,PluginCoder.getCoderGUI().getEqualityGUI().getOperatorItem(operator));
				PluginCoder.getCoderGUI().getEqualityGUI().updateEqualityInstruction();
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==2||event.getSlot()==6){
				if(ChatColor.stripColor(event.getInventory().getItem(event.getSlot()+9).getItemMeta().getDisplayName()).trim().isEmpty())return;
				ItemStack instructionItem=new ItemStack(mainPlugin.getCodeUtils().getVersionedMaterial(Material.FILLED_MAP));
				ItemMeta meta=instructionItem.getItemMeta();
				meta.setDisplayName(ChatColor.DARK_RED+"null");
				if(PluginCoder.getCoderGUI().getEqualityGUI().getSelectedInstructionSlot()-9==event.getSlot()){
					meta.addEnchant(Enchantment.values()[0],1,false);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
				}
				instructionItem.setItemMeta(meta);
				event.getInventory().setItem(event.getSlot()+9,instructionItem);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//text gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getTextGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getTextGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getTextGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>9&&event.getSlot()<17){
				if(event.getCurrentItem()==null)return;
				if(PluginCoder.getCoderGUI().getTextGUI().isDeletingText()){
					event.getInventory().setItem(event.getSlot(),null);
					int textIndex=event.getSlot()-10+PluginCoder.getCoderGUI().getTextGUI().getStartContentIndex();
					PluginCoder.getCoderGUI().getTextGUI().getTextContents().remove(textIndex);
					PluginCoder.getCoderGUI().getTextGUI().renderBar();
					player.updateInventory();
				}else{
					if(PluginCoder.getCoderGUI().getTextGUI().isDeletingText()){
						PluginCoder.getCoderGUI().getTextGUI().setDeletingText(false);
						event.getInventory().getItem(22).setType(Material.BUCKET);
					}
					PluginCoder.getCoderGUI().getTextGUI().getLastIndex().add(event.getSlot()-10+PluginCoder.getCoderGUI().getTextGUI().getStartContentIndex());
					PluginCoder.getCoderGUI().getTextGUI().getIsNewText().add(false);
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					if(mainPlugin.getColorTranslator().keySet().contains(instruction)){
						PluginCoder.getCoderGUI().getTextGUI().cancelColorTask();
						player.openInventory(PluginCoder.getCoderGUI().getTextColorGUI().getGUI());
					}else if(mainPlugin.getCodeExecuter().isExecution(instruction,PluginCoder.getCoderGUI().getExecutionWriterGUI().getVariables())){
						PluginCoder.getCoderGUI().getTextGUI().prepareToNextGUI();
						PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(instruction,event.getClickedInventory());
						player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
					}else{
						PluginCoder.getCoderGUI().getTextGUI().setAddingText(true);
						player.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeText"));
						player.closeInventory();
						PluginCoder.getCoderGUI().getTextGUI().cancelColorTask();
					}
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==18){
				if(PluginCoder.getCoderGUI().getTextGUI().previousContent())PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==26){
				if(PluginCoder.getCoderGUI().getTextGUI().nextContent())PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==22){
				//delete element
				boolean deleting=!PluginCoder.getCoderGUI().getTextGUI().isDeletingText();
				PluginCoder.getCoderGUI().getTextGUI().setDeletingText(deleting);
				ItemStack deleteItem=event.getCurrentItem();
				deleteItem.setType(deleting?Material.LAVA_BUCKET:Material.BUCKET);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==29){
				player.openInventory(PluginCoder.getCoderGUI().getTextColorGUI().getGUI());
				PluginCoder.getCoderGUI().getTextGUI().cancelColorTask();
				PluginCoder.getCoderGUI().getTextGUI().getIsNewText().add(true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==31){
				PluginCoder.getCoderGUI().getTextGUI().getIsNewText().add(true);
				PluginCoder.getCoderGUI().getTextGUI().setAddingText(true);
				PluginCoder.getCoderGUI().getTextGUI().cancelColorTask();
				player.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeText"));
				player.closeInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==33){
				PluginCoder.getCoderGUI().getTextGUI().getIsNewText().add(true);
				PluginCoder.getCoderGUI().getTextGUI().prepareToNextGUI();
				PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI("",event.getClickedInventory());
				player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==39){
				PluginCoder.getCoderGUI().getTextGUI().addNewContent("(");
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==40){
				PluginCoder.getCoderGUI().getTextGUI().addNewContent(" ");
				PluginCoder.getCoderGUI().buttonSound(player);
			} else if(event.getSlot()==41){
				PluginCoder.getCoderGUI().getTextGUI().addNewContent(")");
				PluginCoder.getCoderGUI().buttonSound(player);
			}
            //text color gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getTextColorGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getTextGUI().startColorTask();
				player.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getFunctionGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getCurrentItem()!=null&&event.getSlot()%9!=0&&(event.getSlot()+1)%9!=0){
				PluginCoder.getCoderGUI().getTextColorGUI().saveColor(event.getCurrentItem(),event.getSlot());
				PluginCoder.getCoderGUI().getTextGUI().startColorTask();
				player.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
            //parameters gui
		}else if(event.getInventory().equals(PluginCoder.getCoderGUI().getParametersGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getParametersGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getParametersGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>=10&&event.getSlot()<=16){
				String classType=PluginCoder.getCoderGUI().getParametersGUI().getParamTypes().get(event.getSlot()-10);
				String param=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				if(classType.equals(String.class.getTypeName())){//Text GUI
					PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(event.getSlot());
					PluginCoder.getCoderGUI().getTextGUI().updateInventory(param,event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI());
				}else if(classType.equals(boolean.class.getTypeName())){//Conditions GUI
					PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(event.getSlot());
					PluginCoder.getCoderGUI().getConditionsGUI().updateInventory(param,event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getConditionsGUI().getGUI());
				}else if(PluginCoder.getCoderGUI().getMathGUI().typeIsMath(classType)){//Math GUI
					PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(event.getSlot());
					PluginCoder.getCoderGUI().getMathGUI().updateGUI(param, event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getMathGUI().getGUI());
				}else PluginCoder.getCoderGUI().getParametersGUI().updateParams(classType,event.getSlot());
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if((event.getSlot()>=28&&event.getSlot()<=34||event.getSlot()>=37&&event.getSlot()<=43)){
				if(event.getCurrentItem().getType()==Material.BOOK){
					int methodIndex=PluginCoder.getCoderGUI().getParametersGUI().getElementPage()*14+(event.getSlot()>=37?
							event.getSlot()-30:event.getSlot()-28);
					PluginCoder.getCoderGUI().getParametersGUI().selectMethod(event.getCurrentItem(),methodIndex);
					PluginCoder.getCoderGUI().getParametersGUI().renderContentParams(methodIndex,new ArrayList<>(),false);
					PluginCoder.getCoderGUI().getParametersGUI().updateInstructionSign();
					player.updateInventory();
				}else{
					String param=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					if(param.contains("(")){
						param=param.replaceAll("\\(([^(]+)\\)","()");
						PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(-1);
						PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI(param, event.getClickedInventory());
						player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
					}else{
						PluginCoder.getCoderGUI().getParametersGUI().updateMethodParam(param);
						player.updateInventory();
					}
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==45){
				if(!PluginCoder.getCoderGUI().getParametersGUI().previousPage())return;
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==53){
				if(!PluginCoder.getCoderGUI().getParametersGUI().nextPage())return;
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==4){
				if(!PluginCoder.getCoderGUI().getParametersGUI().hasMoreOptions())return;
				String methodName=ChatColor.stripColor(PluginCoder.getCoderGUI().getParametersGUI().getGUI().getItem(4).getItemMeta().getDisplayName()).trim();
				methodName=methodName.replaceAll("^([^(]+)\\((.*)\\)$","$1()");
				PluginCoder.getCoderGUI().getParametersGUI().updateOptions(methodName);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==48&&event.getCurrentItem().getType()==Material.CHEST){
				PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(-1);
				PluginCoder.getCoderGUI().getExecutionWriterGUI().updateGUI("", event.getClickedInventory());
				player.openInventory(PluginCoder.getCoderGUI().getExecutionWriterGUI().getGui());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==50&&(event.getCurrentItem().getType().toString().equals("CRAFTING_TABLE")||
					event.getCurrentItem().getType().toString().equals("WORKBENCH"))){
				PluginCoder.getCoderGUI().getParametersGUI().prepareToNextGUI(-1);
				PluginCoder.getCoderGUI().getConstructorsGUI().updateInventory(event.getClickedInventory(),"");
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		}//for parameters
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getForParametersGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getForParametersGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getForParametersGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==11){
				PluginCoder.getCoderGUI().getForParametersGUI().setSelectedSlot(11);
				PluginCoder.getCoderGUI().getForParametersGUI().setEditingVar(true);
				PluginCoder.getCoderGUI().buttonSound(player);
				player.sendMessage(ChatColor.YELLOW+PluginCoder.getCoderGUI().getGuiText("writeVarName"));
				player.closeInventory();
			}else if(event.getSlot()==13){
				PluginCoder.getCoderGUI().getForParametersGUI().renderIterable();
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==15){
				ItemStack possibleArrow=event.getInventory().getItem(31);
				if(possibleArrow!=null&&(possibleArrow.getType().toString().equals("PLAYER_HEAD")||
						possibleArrow.toString().equals("SKULL_ITEM"))){
					PluginCoder.getCoderGUI().getForParametersGUI().loadMathGUI(event, player);
				}
			}else if(event.getSlot()==45&&!event.getCurrentItem().getType().toString().contains("GLASS")){
				if(!PluginCoder.getCoderGUI().getForParametersGUI().previousPage())return;
				player.updateInventory();PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==54&&!event.getCurrentItem().getType().toString().contains("GLASS")){
				if(!PluginCoder.getCoderGUI().getForParametersGUI().nextPage())return;
				player.updateInventory();PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if((event.getSlot()>27&&event.getSlot()<35)||(event.getSlot()>36&&event.getSlot()<43)){
				ItemStack possibleArrow=event.getInventory().getItem(31);
				if(possibleArrow!=null&&(possibleArrow.getType().toString().equals("PLAYER_HEAD")||
						possibleArrow.toString().equals("SKULL_ITEM"))){
					if(event.getSlot()==31){
						PluginCoder.getCoderGUI().getForParametersGUI().changeArrow();
						player.updateInventory();PluginCoder.getCoderGUI().buttonSound(player);
					}else if(event.getSlot()==29||event.getSlot()==33)PluginCoder.getCoderGUI().getForParametersGUI().loadMathGUI(event, player);
				}else{ //select iterable
					String iterable=event.getCurrentItem().getItemMeta().getDisplayName();
					ItemStack iterableItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(iterable);
					event.getClickedInventory().setItem(13,iterableItem);
					PluginCoder.getCoderGUI().getForParametersGUI().updateInstructionSign();
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
					ItemStack iteratorItem=event.getInventory().getItem(15);
					iteratorItem.setType(mainPlugin.getVersionNumber()<13? Material.getMaterial("EMPTY_MAP"):Material.MAP);
					ItemMeta meta=iteratorItem.getItemMeta();
					meta.setDisplayName(ChatColor.WHITE+"");
					iteratorItem.setItemMeta(meta);
				}
			}
		}//function parameters
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getFunctionParametersGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getFunctionParametersGUI().returnHome(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==21){
				PluginCoder.getCoderGUI().getFunctionParametersGUI().addParam(player,0);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==23){
				PluginCoder.getCoderGUI().getFunctionParametersGUI().deleteLastParam();
				PluginCoder.getCoderGUI().buttonSound(player);
				player.updateInventory();
			}else if(event.getSlot()>=10&&event.getSlot()<17){
				PluginCoder.getCoderGUI().getFunctionParametersGUI().addParam(player,event.getSlot());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		}
		//constructor gui
		else if(event.getInventory().equals(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getConstructorsGUI().returnPage(player);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getConstructorsGUI().saveChanges(true);
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==4){
				String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				if(instruction.equalsIgnoreCase("null"))return;
				PluginCoder.getCoderGUI().getParametersGUI().updateInventory(instruction,null,event.getClickedInventory());
				if(event.getCurrentItem().getItemMeta().getLore()!=null){
					player.openInventory(PluginCoder.getCoderGUI().getParametersGUI().getGUI());
					PluginCoder.getCoderGUI().buttonSound(player);
				}
			}else if(event.getSlot()==49){
				player.openInventory(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().get(0));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==47){
				String instruction=ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getDisplayName()).trim();
				PluginCoder.getCoderGUI().getListConstructorGUI().updateGUI(instruction);
				player.openInventory(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==51){
				String instruction=ChatColor.stripColor(event.getInventory().getItem(4).getItemMeta().getDisplayName()).trim();
				PluginCoder.getCoderGUI().getDictConstructorGUI().updateGUI(instruction);
				player.openInventory(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==10){
				PluginCoder.getCoderGUI().getConstructorsGUI().updateInstructionSign("Inventory()");
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==11){
				PluginCoder.getCoderGUI().getConstructorsGUI().updateInstructionSign("ItemStack()");
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			else if(event.getSlot()==12){
				PluginCoder.getCoderGUI().getConstructorsGUI().updateInstructionSign("Location()");
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//object constructor gui
		}else if(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().stream().anyMatch(inv->inv.equals(event.getInventory()))){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			int index=PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().indexOf(event.getClickedInventory());
			if(event.getSlot()==0){
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getConstructorsGUI().saveChanges(true);
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==18){
				if(index<=0)return;
				player.openInventory(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().get(index-1));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==26){
				if(index+1==PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().size())return;
				player.openInventory(PluginCoder.getCoderGUI().getObjectConstructorsGUI().getGUI().get(index+1));
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()>9&&event.getSlot()<17){
				String objectName=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
				PluginCoder.getCoderGUI().getConstructorsGUI().saveToConstructorsGUI(objectName,false);
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//List constructor gui
		}else if(PluginCoder.getCoderGUI().getListConstructorGUI().getGUI().equals(event.getInventory())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getListConstructorGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getListConstructorGUI().saveChanges(true);
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==18){
				if(PluginCoder.getCoderGUI().getListConstructorGUI().previousElement()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==26){
				if(PluginCoder.getCoderGUI().getListConstructorGUI().nextElement()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()>9&&event.getSlot()<17){
				PluginCoder.getCoderGUI().getListConstructorGUI().setNewElement(false);
				PluginCoder.getCoderGUI().getListConstructorGUI().setElementSlot(event.getSlot());
				if(!PluginCoder.getCoderGUI().getListConstructorGUI().isDeletingElement()){
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					PluginCoder.getCoderGUI().getSetValueGUI().updateGUI(instruction,event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				}else{
					PluginCoder.getCoderGUI().getListConstructorGUI().removeElement(event.getSlot());
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==24){
				boolean deleting=!PluginCoder.getCoderGUI().getListConstructorGUI().isDeletingElement();
				PluginCoder.getCoderGUI().getListConstructorGUI().setDeletingElement(deleting);
				ItemStack deleteItem=event.getCurrentItem();
				deleteItem.setType(deleting?Material.LAVA_BUCKET:Material.BUCKET);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==20){
				PluginCoder.getCoderGUI().getListConstructorGUI().setNewElement(true);
				PluginCoder.getCoderGUI().getSetValueGUI().updateGUI("",event.getClickedInventory());
				player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
			//dict constructor gui
		}else if(PluginCoder.getCoderGUI().getDictConstructorGUI().getGUI().equals(event.getInventory())){
			event.setCancelled(true);
			if(event.getClickedInventory().getType()==InventoryType.PLAYER)return;
			if(event.getSlot()==0){
				PluginCoder.getCoderGUI().getDictConstructorGUI().saveChanges(false);
				player.openInventory(PluginCoder.getCoderGUI().getConstructorsGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==8){
				PluginCoder.getCoderGUI().getDictConstructorGUI().saveChanges(true);
				PluginCoder.getCoderGUI().returnHome(player,true);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==27){
				if(PluginCoder.getCoderGUI().getDictConstructorGUI().previousElement()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()==35){
				if(PluginCoder.getCoderGUI().getDictConstructorGUI().nextElement()){
					PluginCoder.getCoderGUI().buttonSound(player);
					player.updateInventory();
				}
			}else if(event.getSlot()>9&&event.getSlot()<17||event.getSlot()>18&&event.getSlot()<26){
				PluginCoder.getCoderGUI().getDictConstructorGUI().setNewElement(false);
				if(event.getSlot()<18)PluginCoder.getCoderGUI().getDictConstructorGUI().setIsKey(true);
				else PluginCoder.getCoderGUI().getDictConstructorGUI().setIsKey(false);
				PluginCoder.getCoderGUI().getDictConstructorGUI().setElementSlot(event.getSlot());
				if(!PluginCoder.getCoderGUI().getDictConstructorGUI().isDeletingElement()){
					String instruction=ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName()).trim();
					PluginCoder.getCoderGUI().getSetValueGUI().updateGUI(instruction,event.getClickedInventory());
					player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				}else{
					PluginCoder.getCoderGUI().getDictConstructorGUI().removeElement(event.getSlot());
				}
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==31){
				boolean deleting=!PluginCoder.getCoderGUI().getDictConstructorGUI().isDeletingElement();
				PluginCoder.getCoderGUI().getDictConstructorGUI().setDeletingElement(deleting);
				ItemStack deleteItem=event.getCurrentItem();
				deleteItem.setType(deleting?Material.LAVA_BUCKET:Material.BUCKET);
				player.updateInventory();
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==29){
				PluginCoder.getCoderGUI().getDictConstructorGUI().setNewElement(true);
				PluginCoder.getCoderGUI().getDictConstructorGUI().setIsKey(true);
				PluginCoder.getCoderGUI().getSetValueGUI().updateGUI("",event.getClickedInventory());
				player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(event.getSlot()==33){
				PluginCoder.getCoderGUI().getDictConstructorGUI().setNewElement(true);
				PluginCoder.getCoderGUI().getDictConstructorGUI().setIsKey(false);
				PluginCoder.getCoderGUI().getSetValueGUI().updateGUI("",event.getClickedInventory());
				player.openInventory(PluginCoder.getCoderGUI().getSetValueGUI().getGUI());
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		}
	}
	@EventHandler
	public void alClickearInventarioGui(InventoryDragEvent event){
		if(!(event.getWhoClicked()instanceof Player))return;
		Player player= (Player) event.getWhoClicked();
		if(!PluginCoder.getCoderGUI().getFunctionGUI().getGUI().contains(event.getInventory()))return;
		event.setCancelled(true);
		int slot=event.getInventorySlots().stream().findFirst().orElse(-1);
		if(slot>=44)return;
		ItemStack cursor=event.getOldCursor();
		int inventoryIndex=PluginCoder.getCoderGUI().getFunctionGUI().getGUI().indexOf(event.getInventory());
		int instructionIndex=inventoryIndex*28+slot-(10+2*(slot/9-1));
		Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin,()->{
			if(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex()>=0){
				PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionInFunction(PluginCoder.getCoderGUI().getFunctionGUI().getMoveInstructionIndex(),instructionIndex,true);
				PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionItem(cursor,inventoryIndex,slot);
				player.setItemOnCursor(null);player.updateInventory();
				PluginCoder.getCoderGUI().getFunctionGUI().setMoveInstructionIndex(-2);
				PluginCoder.getCoderGUI().buttonSound(player);
			}else if(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex()>=0){
				PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionInFunction(PluginCoder.getCoderGUI().getFunctionGUI().getCopyInstructionIndex(),instructionIndex,false);
				PluginCoder.getCoderGUI().getFunctionGUI().insertInstructionItem(cursor,inventoryIndex,slot);
				player.setItemOnCursor(null);player.updateInventory();
				PluginCoder.getCoderGUI().getFunctionGUI().setCopyInstructionIndex(-2);
				PluginCoder.getCoderGUI().buttonSound(player);
			}
		},1);
	}
	private void changeLanguage(Player p, Language language){
		PluginCoder.getCoderGUI().buttonSound(p);
		if(mainPlugin.getLanguage()==language){
			p.openInventory(mainPlugin.getCoderGUI().getPluginCoderGUI());
			return;
		}
		mainPlugin.updateLanguage(language);
		PluginCoder.getCoderGUI().updateInventories();
		p.openInventory(mainPlugin.getCoderGUI().getPluginCoderGUI());
	}
	@EventHandler
	public void alEscribirStringEnChat(AsyncPlayerChatEvent event){
		Player p=event.getPlayer();
		ErrorManager.setSender(p);
		//editar nombre de variable
		if(PluginCoder.getCoderGUI().getVariableGUI().isEditingVarName()){
			PluginCoder.getCoderGUI().getVariableGUI().setEditingVarName(false);
			event.setCancelled(true);
			String newVarName=event.getMessage();
			boolean error=!ErrorManager.checkTextVariable(newVarName,"",mainPlugin)||newVarName.isEmpty();
			if(PluginCoder.getCoderGUI().getVariableGUI().isNewVar()){
				PluginCoder.getCoderGUI().getVariableGUI().setNewVar(false);
				if(!error){
					if(PluginCoder.getCoderGUI().getFunctionGUI().addInstruction(newVarName+"=null"))Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
							p.openInventory(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().get(PluginCoder.getCoderGUI().getFunctionGUI().getGUI().size()-1)), 1);
					return;
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
						p.openInventory(PluginCoder.getCoderGUI().getInstructionsGUI().getGui()), 1);
			}else{
				if(!error){
					String oldVarName=PluginCoder.getCoderGUI().getVariableGUI().getVarInstruction().replaceAll("^(.+)\\=(.*)$","$1");
					if(!newVarName.equals(oldVarName)){
						ItemStack varNameItem=new ItemStack(mainPlugin.getVersionNumber()<14?Material.getMaterial("SIGN"):Material.OAK_SIGN);
						ItemMeta meta=varNameItem.getItemMeta();
						meta.setDisplayName(ChatColor.GOLD+newVarName);
						varNameItem.setItemMeta(meta);
						PluginCoder.getCoderGUI().getVariableGUI().getGui().setItem(20,varNameItem);
					}
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
						p.openInventory(PluginCoder.getCoderGUI().getVariableGUI().getGui()), 1);
			}
			//editar nombre de los plugins
		}else if(mainPlugin.getCoderGUI().getPluginsGUI().isEditingPluginName()){
			event.setCancelled(true);
			String newPluginName=event.getMessage();
			int selectedIndex=mainPlugin.getCoderGUI().getPluginsGUI().getPluginSelectedIndex();
			if(selectedIndex!=-1){
				Plugin plugin=mainPlugin.getPlugins().get(selectedIndex);
				boolean error=!PluginCoder.getCoderGUI().getPluginsGUI().checkPluginName(newPluginName,plugin);
				if(!error){
					File pluginFile=new File(mainPlugin.getDataFolder().getParentFile().getPath()+
							"/PluginCoder/plugins/"+plugin.getName()+".txt");
					pluginFile.renameTo(new File(mainPlugin.getDataFolder().getParentFile().getPath()+
							"/PluginCoder/plugins/"+newPluginName+".txt"));
					plugin.setName(newPluginName);
					PluginCoder.getCoderGUI().getPluginsGUI().updatePluginEditor(plugin);
				}
				if(plugin.getName().equals(mainPlugin.getSelectedPlugin().getName())){
					PluginCoder.getCoderGUI().updatePluginItem(plugin.getName());
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
						p.openInventory(PluginCoder.getCoderGUI().getPluginsGUI().getPluginEditor()), 1);
			}else{
				boolean error=!mainPlugin.getCoderGUI().getPluginsGUI().checkPluginName(newPluginName,null);
				if(!error){
					mainPlugin.createNewPlugin(newPluginName);	PluginCoder.getCoderGUI().getPluginsGUI().updateGUI();
				}
				Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
						p.openInventory(PluginCoder.getCoderGUI().getPluginsGUI().getGUI().get(
								mainPlugin.getCoderGUI().getPluginsGUI().getLastOpenedPage()
						)), 1);
			}
			mainPlugin.getCoderGUI().getPluginsGUI().setEditingPluginName(false);
			//command prompt gui
		}else if(PluginCoder.getCoderGUI().getCommandPromptGUI().getNewArgumentType()!=null){
			event.setCancelled(true);
			String newArgName=event.getMessage().trim();
			if(!newArgName.isEmpty()){
				Command command=PluginCoder.getCoderGUI().getCommandPromptGUI().getCommand();
				if(command.getPrompt().isEmpty())command.setPrompt(newArgName);
				else command.setPrompt(command.getPrompt()+" "+newArgName);
				CommandVarType varType=PluginCoder.getCoderGUI().getCommandPromptGUI().getNewArgumentType();
				if(varType!=CommandVarType.NONE)command.getCommandVars().put(newArgName,CommandVarType.valueOf(varType.toString()));
				PluginCoder.getCoderGUI().getCommandPromptGUI().renderBar();
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
							p.openInventory(PluginCoder.getCoderGUI().getCommandPromptGUI().getGUI()), 1);
			PluginCoder.getCoderGUI().getCommandPromptGUI().setNewArgumentType(null);

		}//text gui
		else if(PluginCoder.getCoderGUI().getTextGUI().isAddingText()){
			event.setCancelled(true);
			String text=event.getMessage().replace("+","(+)");
			PluginCoder.getCoderGUI().getTextGUI().saveToTextGUI(text,false,false);
			PluginCoder.getCoderGUI().getTextGUI().startColorTask();
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
					p.openInventory(PluginCoder.getCoderGUI().getTextGUI().getGUI()), 1);
			PluginCoder.getCoderGUI().getTextGUI().setAddingText(false);
		}//forParameters gui
		else if(PluginCoder.getCoderGUI().getForParametersGUI().isEditingVar()){
			event.setCancelled(true);
			PluginCoder.getCoderGUI().getForParametersGUI().setEditingVar(false);
			String varName=	event.getMessage();
			PluginCoder.getCoderGUI().getForParametersGUI().saveToForParametersGUI(varName,false);
			PluginCoder.getCoderGUI().getForParametersGUI().setSelectedSlot(0);
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
					p.openInventory(PluginCoder.getCoderGUI().getForParametersGUI().getGUI()), 1);
		}else if(PluginCoder.getCoderGUI().getObjectsGUI().isCreatingObject()){
			PluginCoder.getCoderGUI().getObjectsGUI().setCreatingObject(false);
			event.setCancelled(true);
			String objectName=event.getMessage();
			objectName=(objectName.charAt(0)+"").toUpperCase()+objectName.substring(1,objectName.length());
			if(objectName.equalsIgnoreCase("Plugin"))p.sendMessage(ChatColor.RED+ErrorManager.getErrorTranslation().get("pluginNameReserved"));
			else{
				boolean error=false;
				for(PluginObject object:mainPlugin.getSelectedPlugin().getObjects()){
					if(object.getName().equals(objectName)){
						p.sendMessage(ChatColor.RED+ErrorManager.getErrorTranslation().get("existingObject"));
						error=true;break;
					}
				}
				if(!error){
					PluginObject newObject=new PluginObject(mainPlugin.getSelectedPlugin(),objectName);
					mainPlugin.getSelectedPlugin().getObjects().add(newObject);
					PluginCoder.getCoderGUI().getObjectsGUI().addObject(newObject);
					PluginCoder.getCoderGUI().getCheckObjectTypeGUI().addObjectToTypeList(objectName);
					PluginCoder.getCoderGUI().getCheckObjectTypeGUI().sortObjectTypes();
					PluginCoder.getCoderGUI().getObjectConstructorsGUI().updateGUI(); //actualizar constructores de objeto
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
					p.openInventory(PluginCoder.getCoderGUI().getObjectsGUI().getGUI().get(
							PluginCoder.getCoderGUI().getObjectsGUI().getGUI().size()-1
					)), 1);
			//add property to object
		}else if(PluginCoder.getCoderGUI().getObjectGUI().isAddingItem()){
			PluginCoder.getCoderGUI().getObjectGUI().setAddingItem(false);
			event.setCancelled(true);
			String newItemName=event.getMessage();
			PluginObject object=PluginCoder.getCoderGUI().getObjectGUI().getObject();
			if(PluginCoder.getCoderGUI().getObjectGUI().getRenderedSlot()==13){
				String newFunction=newItemName+"{}";
				object.getDeclaredFunctions().add(newFunction);
				ItemStack functionItem=PluginCoder.getCoderGUI().getFunctionGUI().getInstructionItem(newFunction);
				PluginCoder.getCoderGUI().getObjectGUI().addItem(functionItem);
			}else{
				boolean error=!ErrorManager.checkTextVariable(newItemName,"",mainPlugin)||newItemName.isEmpty();
				if(!error){
					if(object.getProperties().stream().anyMatch(prop->prop.equals(newItemName))){
						p.sendMessage(ChatColor.RED+ErrorManager.getErrorTranslation().get("existingProperty"));
					}else{
						object.getProperties().add(newItemName);
						ItemStack propertyItem=PluginCoder.getCoderGUI().getObjectGUI().getPropertyItem(newItemName,null);
						PluginCoder.getCoderGUI().getObjectGUI().addItem(propertyItem);
					}
				}else{
					p.sendMessage(ChatColor.RED+ErrorManager.getErrorTranslation().get("invalidProperty"));
				}
			}
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
					p.openInventory(PluginCoder.getCoderGUI().getObjectGUI().getGUI()), 1);
		}else if(PluginCoder.getCoderGUI().getFunctionParametersGUI().isAddingParam()){
			PluginCoder.getCoderGUI().getFunctionParametersGUI().setAddingParam(false);
			event.setCancelled(true);
			String newParamName=event.getMessage();
			int paramSlot=PluginCoder.getCoderGUI().getFunctionParametersGUI().getEditingParamSlot();
			Inventory gui=PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI();
			boolean error=false;
			for(int i=0;i<7;i++){
				if(paramSlot==10+i)continue;
				ItemStack paramItem=gui.getItem(10+i);
				if(paramItem==null)break;
				String paramName=ChatColor.stripColor(paramItem.getItemMeta().getDisplayName()).trim();
				if(paramName.equalsIgnoreCase(newParamName)){
					error=true;break;
				}
			}
			if(!error){
				ItemStack paramItem=PluginCoder.getCoderGUI().getObjectGUI().getPropertyItem(newParamName,"");
				if(paramSlot==0)gui.addItem(paramItem);
				else gui.setItem(paramSlot,paramItem);
			}else p.sendMessage(ChatColor.RED+ErrorManager.getErrorTranslation().get("existingParam"));
			Bukkit.getScheduler().scheduleSyncDelayedTask(mainPlugin, () ->
					p.openInventory(PluginCoder.getCoderGUI().getFunctionParametersGUI().getGUI()), 1);
			PluginCoder.getCoderGUI().getFunctionParametersGUI().updateFunctionSign();
		}
		ErrorManager.setSender(Bukkit.getConsoleSender());
	}
}