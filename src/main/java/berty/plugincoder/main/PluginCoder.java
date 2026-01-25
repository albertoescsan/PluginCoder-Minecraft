package berty.plugincoder.main;

import java.io.File;
import java.util.*;

import berty.plugincoder.utils.CodeUtils;
import berty.plugincoder.writer.PluginFileWriter;
import berty.plugincoder.interpreter.objects.PluginMethod;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.GUI.guis.CoderGUI;
import berty.plugincoder.interpreter.Language;
import berty.plugincoder.GUI.guis.function.*;
import berty.plugincoder.GUI.listener.PluginGuiListener;
import berty.plugincoder.GUI.listener.PluginGuiSavingListener;
import berty.plugincoder.command.PCCommand;
import berty.plugincoder.interpreter.conditionals.PluginConditionals;
import berty.plugincoder.interpreter.containers.PluginContainers;
import berty.plugincoder.interpreter.executer.CodeExecuter;
import berty.plugincoder.compiler.PluginCompiler;
import berty.plugincoder.GUI.InicVars;
import berty.plugincoder.interpreter.listener.PluginListener;
import berty.plugincoder.interpreter.logic.PluginLogic;
import berty.plugincoder.interpreter.math.PluginMath;
import berty.plugincoder.interpreter.loops.PluginFor;
import berty.plugincoder.interpreter.loops.PluginWhile;
import berty.plugincoder.reader.PluginFileReader;
import berty.plugincoder.interpreter.tasks.PluginDelay;
import berty.plugincoder.interpreter.tasks.PluginRepeat;
import berty.plugincoder.predictor.PredictType;
import berty.plugincoder.interpreter.translators.Translators;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginCoder extends JavaPlugin{

	private int versionNumber;

	private Language language=Language.ENGLISH;
	private List<Plugin> plugins=new ArrayList<>();
	private Plugin selectedPlugin;
	private List<PluginMethod> methods=new ArrayList<>();
	private Map<String,String> colorTranslator=new HashMap<>();
	private Map<String,Class> constructorTranslator=new HashMap<>();
	private List<String> reservedWords;
	private CodeUtils codeUtils;
	private static boolean errorFound;
	private static CoderGUI coderGUI;
	private CodeExecuter codeExecuter=new CodeExecuter(this);
	private PluginFileReader reader=new PluginFileReader(this);
	private PluginFileWriter writer=new PluginFileWriter(this);
	private PluginMath math=new PluginMath(this);
	private PluginLogic logic=new PluginLogic(this);
	private PluginConditionals conditionals=new PluginConditionals(this);
	private PluginFor forManager=new PluginFor(this);
	private PluginWhile whileManager=new PluginWhile(this);
	private PluginDelay delay=new PluginDelay(this);
	private PluginRepeat repeat=new PluginRepeat(this);
	private PluginContainers containers=new PluginContainers(this);

	public void onEnable() {
		versionNumber= Integer.parseInt(Bukkit.getVersion().replaceAll("^(.+)\\(MC: 1\\.([^\\.]+)(.*)\\)$","$2"));
		PluginCompiler.mainPlugin=this;
		PredictType.mainPlugin=this;
		InicVars.mainPlugin=this;
		codeUtils=new CodeUtils(this);
		coderGUI=new CoderGUI(this);
		this.getServer().getPluginManager().registerEvents(new PluginListener(this),this);
		this.getServer().getPluginManager().registerEvents(new PluginGuiListener(this),this);
		this.getServer().getPluginManager().registerEvents(new PluginGuiSavingListener(this),this);
		this.getCommand("plugincoder").setExecutor(new PCCommand(this));
		//traducciones de metodos spigot y java
		Translators.registerMethods();
		Translators.registerConstructors(constructorTranslator);
		String language=this.getConfig().getString("Language");
		if(language!=null)this.language=Language.valueOf(language);
		reader.readLanguage();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+PluginCoder.getCoderGUI().getGuiText("startMessage"));
		//traducciones de colores para strings
		colorTranslator.put("DARK_RED","§4");
		colorTranslator.put("RED","§c");
		colorTranslator.put("ORANGE","§6");
		colorTranslator.put("YELLOW","§e");
		colorTranslator.put("GREEN","§a");
		colorTranslator.put("DARK_GREEN","§2");
		colorTranslator.put("AQUA","§b");
		colorTranslator.put("CYAN","§3");
		colorTranslator.put("BLUE","§9");
		colorTranslator.put("DARK_BLUE","§1");
		colorTranslator.put("MAGENTA","§d");
		colorTranslator.put("PURPLE","§5");
		colorTranslator.put("WHITE","§f");
		colorTranslator.put("GRAY","§7");
		colorTranslator.put("DARK_GRAY","§8");
		colorTranslator.put("BLACK","§0");
		colorTranslator.put("BOLD","§l");
		colorTranslator.put("ITALIC","§o");
		colorTranslator.put("UNDERLINE","§n");
		colorTranslator.put("STRIKE","§m");
		colorTranslator.put("RESET","§r");
		//palabras reservadas
		reservedWords = new ArrayList<>(Arrays.asList("and", "or", "if", "else", "true", "false", "for",
				"while", "return", "delay", "repeat", "stop", "continue",
				"plugin", "server"
		));
		reservedWords.addAll(colorTranslator.keySet());
		//cargar los plugins
		reader.registerPlugins();
		String selectedPluginName=this.getConfig().getString("SelectedPlugin");
		if(selectedPluginName!=null)selectedPlugin=getPlugin(selectedPluginName);
		if(selectedPlugin==null){
			Optional<Plugin> plugin=plugins.stream().filter(p->p.getName().equals("Plugin")).findFirst();
			if(plugin.isPresent())selectedPlugin=plugin.get();
			else selectedPlugin=createNewPlugin("Plugin");
		}
		//ejecutar enable function
		for(Plugin plugin:plugins){
			if(!plugin.isEnabled())continue;
			PluginConfig.load(plugin);
			Map<String,Object> variables=getPluginVars(plugin);
			variables.putAll(plugin.getMainObjectInstance().getProperties());
			codeExecuter.executeFunction(plugin.getActivationFunction(),"",variables);
			if(errorFound)continue;
			variables.keySet().stream().filter(var->plugin.getMainObjectInstance().getProperties().containsKey(var))
					.forEach(var->plugin.getMainObjectInstance().getProperties().put(var,variables.get(var)));
		}
		//comprobar carpeta generators
		File generator= new File(this.getDataFolder().getPath()+"/generator");
		if(!generator.exists())generator.mkdirs();
	}
	public void onDisable() {
		FileConfiguration config=this.getConfig();
		config.set("SelectedPlugin",selectedPlugin.getName());
		config.set("Language",language.toString());
		this.saveConfig();
		//ejecutar disable function
		for(Plugin plugin:plugins){
			if(!plugin.isEnabled())continue;
			Map<String,Object> variables=getPluginVars(plugin);
			variables.putAll(plugin.getMainObjectInstance().getProperties());
			codeExecuter.executeFunction(plugin.getDeactivationFunction(),"",variables);
			writer.savePlugin(plugin);
			PluginConfig.save(plugin);
		}
	}
	public Plugin createNewPlugin(String name){
		Plugin plugin=new Plugin(name);
		reader.readCode(plugin);
		if(plugin.getActivationContainer().isEmpty())plugin.getActivationContainer().add("Activation{}");
		if(plugin.getDeactivationContainer().isEmpty())plugin.getDeactivationContainer().add("Deactivation{}");
		plugins.add(plugin);
		return plugin;
	}
	public Plugin getPlugin(String name){
		return plugins.stream().filter(plugin->plugin.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	public PluginMethod getMethod(String name){
		return methods.stream().filter(method->method.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	public Map<String,Object> getPluginVars(Plugin plugin){
		Map<String,Object> pluginVars=new HashMap<>();
		pluginVars.put("plugin",plugin.getMainObjectInstance());
		pluginVars.put("server", Bukkit.getServer());
		//TODO añadir más variables si es necesario
		return pluginVars;
	}
	public Map<String,String> getPluginVarTypes(){
		Map<String,String> pluginVars=new HashMap<>();
		pluginVars.put("plugin","PluginObject.Plugin");
		pluginVars.put("server", Bukkit.getServer().getClass().getTypeName());
		//TODO añadir más variables si es necesario
		return pluginVars;
	}
	public void updateLanguage(Language language){
		this.language=language;
		reader.readLanguage();
	}
	public Map<String, List<String>> getMethodTranslator() {
		Map<String, List<String>> methodTranslator=new HashMap<>();
		methods.forEach(method->methodTranslator.put(method.getName(),new ArrayList<>(method.getTranslatedMethodClasses().keySet())));
		return methodTranslator;
	}
	public List<Plugin> getPlugins() {
		return plugins;
	}

	public Plugin getSelectedPlugin() {
		return selectedPlugin;
	}

	public void setSelectedPlugin(Plugin selectedPlugin) {
		this.selectedPlugin = selectedPlugin;
	}
	public int getVersionNumber() {
		return versionNumber;
	}

	public Language getLanguage() {
		return language;
	}

	public CodeUtils getCodeUtils() {
		return codeUtils;
	}
	public static boolean isErrorFound() {
		return errorFound;
	}
	public static void setErrorFound(boolean b) {
		errorFound = b;
	}
	public List<PluginMethod> getMethods() {
		return methods;
	}

	public CodeExecuter getCodeExecuter() {
		return codeExecuter;
	}
	public PluginMath getMath() {
		return math;
	}
	public PluginLogic getLogic() {
		return logic;
	}
	public List<String> getReservedWords() {
		return reservedWords;
	}
	public Map<String, String> getColorTranslator() {
		return colorTranslator;
	}

	public Map<String, Class> getConstructorTranslator() {
		return constructorTranslator;
	}
	public PluginConditionals getConditionals() {
		return conditionals;
	}
	public PluginDelay getDelay() {
		return delay;
	}
	public PluginRepeat getRepeat() {
		return repeat;
	}
	public PluginFileReader getReader() {
		return reader;
	}
	public PluginContainers getContainers() {
		return containers;
	}
	public PluginFor getForManager() {
		return forManager;
	}
	public PluginWhile getWhileManager() {
		return whileManager;
	}
	public static CoderGUI getCoderGUI() {
		return coderGUI;
	}
}
