package berty.plugincoder.command;

import org.bukkit.ChatColor;
import berty.plugincoder.generator.PluginGenerator;
import berty.plugincoder.interpreter.Language;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.GUI.InicVars;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import berty.plugincoder.main.PluginCoder;

import java.io.File;
import java.util.*;

public class PCCommand implements CommandExecutor, TabCompleter {

	private PluginCoder plugin;
	public PCCommand(PluginCoder pluginCoder) {
		plugin=pluginCoder;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command comando, String label, String[] args) {
		if(args.length==0)return true;
		if(args[0].equalsIgnoreCase("menu")){
			if(!(sender instanceof Player))return true;
			Player p= (Player) sender;
			if(p.isOp()||p.hasPermission("pc.admin")){
				if(PluginCoder.getCoderGUI().getGuiPlayer()!=null){
					ErrorManager.somePlayerHasMenuOpened(p);
				}else {
					PluginCoder.getCoderGUI().setGuiPlayer(p);
					p.openInventory(plugin.getCoderGUI().getPluginCoderGUI());
				}
			}
			return true;
		}
		if(args.length<2)return true;
		if(args[0].equalsIgnoreCase("command")){
			//comandos personalizados ejemplo: /give {player} diamond
			//tipos de variable: player, text, number, boolean
			//variables fijas: commandExecuter(Player o Console)
			String prompt="";
			ErrorManager.setSender(sender);
			for(int i=1;i<args.length;i++)prompt+=args[i]+" ";
			prompt=prompt.trim();
			List<Object> executedCommandPlugin=findCommand(prompt);
			if(executedCommandPlugin==null)return true;
			berty.plugincoder.interpreter.command.Command executedCommand= (berty.plugincoder.interpreter.command.Command) executedCommandPlugin.get(0);
			Plugin pl= (Plugin) executedCommandPlugin.get(1);
			if(executedCommand==null)return true;
			Map<String,Object> variables=InicVars.getCommandVars(executedCommand,prompt);
			variables.putAll(plugin.getPluginVars(pl));
			variables.put("sender",sender);
			plugin.getCodeExecuter().executeFunction(executedCommand.getFunction(),"",variables);
			ErrorManager.setSender(Bukkit.getConsoleSender());
		}else if(args[0].equalsIgnoreCase("generate")){
			if(PluginGenerator.generate(args[1]))sender.sendMessage(ChatColor.GREEN+"Plugin generated");//TODO traducir
		}
		else if(args[0].equalsIgnoreCase("language")){
			if(args[1].equalsIgnoreCase("english"))plugin.updateLanguage(Language.ENGLISH);
			else if(args[1].equalsIgnoreCase("español"))plugin.updateLanguage(Language.SPANISH);
			else if(args[1].equalsIgnoreCase("português"))plugin.updateLanguage(Language.PORTUGUESE);
			else if(args[1].equalsIgnoreCase("français"))plugin.updateLanguage(Language.FRENCH);
			else if(args[1].equalsIgnoreCase("deutsch"))plugin.updateLanguage(Language.GERMAN);
			else if(args[1].equalsIgnoreCase("italiano"))plugin.updateLanguage(Language.ITALIAN);
			else if(args[1].equalsIgnoreCase("pусский"))plugin.updateLanguage(Language.RUSSIAN);
			PluginCoder.getCoderGUI().updateInventories();
		}
		return true;
	}
	@Override
	public List<String> onTabComplete(CommandSender sender, Command comando, String label, String[] args) {
		//lista de seleccion
		List<String> options=new ArrayList<>();
		if(args.length==1){
			options.add("command");options.add("generate");options.add("language");options.add("menu");
			return options;
		}
		if(args.length<1)return options;
		if(args[0].equalsIgnoreCase("command")){
			String prompt="";
			for(int i=1;i<args.length;i++)prompt+=args[i]+" ";
			prompt=prompt.trim();
			for(Plugin pl:plugin.getPlugins()){
				for(berty.plugincoder.interpreter.command.Command command:pl.getCommands()){
					String[] commandArgs=command.getPrompt().split(" ");
					if(args.length-1>commandArgs.length)continue;
					if(!command.getPrompt().startsWith(prompt))continue;
					String arg=commandArgs[args.length-2];
					if(command.getCommandVars().get(arg)== CommandVarType.PLAYER){
						for(Player player:Bukkit.getOnlinePlayers())options.add(player.getName());
					}else if(command.getCommandVars().get(arg)==CommandVarType.BOOLEAN){
						options.add("true");options.add("false");
					}else options.add(arg);
				}
			}
		}else if(args[0].equalsIgnoreCase("generate")){
			for(File generator: new File(plugin.getDataFolder().getParentFile().getPath()+"/PluginCoder/generator").listFiles()){
				if (!generator.getName().toLowerCase().endsWith(".txt"))continue;
				options.add(generator.getName().substring(0,generator.getName().length()-4));
			}
		}else if(args[0].equalsIgnoreCase("language")){
			options.add("english");options.add("español");options.add("português");options.add("français");
			options.add("deutsch");options.add("italiano");options.add("pусский");
		}
		return options;
	}
	private List<Object> findCommand(String prompt){
		String[] promptElements=prompt.split(" ");
		for(Plugin pl: plugin.getPlugins()){
			if(!pl.isEnabled())continue;
			for(berty.plugincoder.interpreter.command.Command command:pl.getCommands()){
				String[] commandElements=command.getPrompt().split(" ");
				if(commandElements.length!=promptElements.length)continue;
				boolean commandFound=true;
				for(int i=0;i<commandElements.length;i++){
					String commandElement=commandElements[i];
					if(command.getCommandVars().get(commandElement)!=null)continue;
					String promptElement=promptElements[i];
					if(!promptElement.equalsIgnoreCase(commandElement)){
						commandFound=false;break;
					}
				}
				if(commandFound)return Arrays.asList(command,pl);
			}
		}
		return null;
	}
}
