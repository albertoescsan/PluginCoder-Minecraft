package berty.plugincoder.reader;

import java.io.*;


import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.interpreter.Language;
import berty.plugincoder.interpreter.command.CommandVarType;
import berty.plugincoder.main.PluginCoder;

public class PluginFileReader {

	private PluginCoder mainPlugin;
	public PluginFileReader(PluginCoder plugin) {
		mainPlugin=plugin;
	}
	
	public static List<String> linesFromFile(String file, String charSet) {
		List<String> lineas = null;
		try {
			lineas = Files.readAllLines(Paths.get(file), Charset.forName(charSet));
		} catch (IOException e) {e.printStackTrace();}
		return lineas;
	}
	public void registerPlugins() {
		File dir = new File(mainPlugin.getDataFolder().getPath()+"/plugins");
		if (!dir.exists())dir.mkdirs();
		if (!dir.isDirectory())return;
		if (dir.listFiles() == null)return;
		for (File archivo : dir.listFiles()) {
			if (!archivo.getName().toLowerCase().endsWith(".txt"))continue;
			mainPlugin.createNewPlugin(archivo.getName().substring(0,archivo.getName().length()-4));
		}
	}
	public void readCode(Plugin plugin) {
		File file=new File(mainPlugin.getDataFolder().getPath()+"/plugins/"+plugin.getName()+".txt");
		if(!file.exists()){
			try{file.createNewFile();return;}
			catch (Exception e){e.printStackTrace();}
		}
		String code="";
		int keys=0;
		boolean firstKey=false;
		for(String line:this.linesFromFile(file.getPath(), "UTF-8")) {
			boolean includeSpace=false;
			for(Character c:line.toCharArray()) {
				if(c.equals(' ')) {
					if(includeSpace)code+=String.valueOf(c);
					continue;
				}
				includeSpace=true;
				code+=String.valueOf(c);
				if(c.equals('{')) {
					keys++;firstKey=true;
				}
				else if(c.equals('}'))keys--;
				else if(c.equals(';'))includeSpace=false;
				if(keys==0&&firstKey) {
					this.sortCode(code,plugin);
					firstKey=false;
					code="";
				}
			}
		}
	}
	public void sortCode(String code,Plugin plugin) {
		code=code.replaceAll("^\\s*", "");
		if(code.startsWith("Listener")) {
			getListenerFunctions(plugin.getListener(), code.substring("Listener".length()+1,code.length()-1));
		}else if(code.startsWith("Objects")){
			getObjects(plugin, code.substring("Objects".length()+1,code.length()-1));
		}else if(code.startsWith("Commands")){
			getCommands(plugin, code.substring("Commands".length()+1,code.length()-1));
		}else if(code.startsWith("Activation")){
			saveADFunction(plugin,plugin.getActivationContainer(),code.substring("Activation".length()+1,code.length()-1));
		}else if(code.startsWith("Deactivation")){
			saveADFunction(plugin,plugin.getDeactivationContainer(),code.substring("Deactivation".length()+1,code.length()-1));
		}
	}
	private void saveADFunction(Plugin plugin,List<String> container,String function){
		if(function.trim().isEmpty())return;
		String functionIndex=container==plugin.getActivationContainer()?"Activation":"Deactivation";
		container.add(functionIndex+"{"+function+"}");
	}
	private void getCommands(Plugin plugin, String code) {
		List<String> commands=new ArrayList<>();
		getElementsWithCodeInKeys(commands,code,false);
		for(String commandCode:commands){
			List<String> commandCompoments=new ArrayList<>();
			getElementsWithCodeInKeys(commandCompoments,commandCode.substring("command{".length(),commandCode.length()-1),false);
			String prompt="";
			Map<String,CommandVarType> commandVars=new HashMap<>();
			String functionContent="";
			for(String component:commandCompoments){
				if(component.toLowerCase().matches("^prompt\\s*=\\s*\\{(.*)$")){
					prompt=component.substring(component.length()
							-component.replaceAll("prompt\\s*=\\s*\\{(.*)$","$1").length(),component.length()-1);
					if(prompt.trim().startsWith("/"))prompt=prompt.substring(1,prompt.length());
				}else if(component.toLowerCase().matches("^vars\\s*=\\s*\\{(.*)$")){
					String[]varsContents=component.substring(component.length()
							-component.replaceAll("vars\\s*=\\s*\\{(.*)$","$1").length(),component.length()-1).split(",");
					for(String varContent:varsContents){
						String[] varType=varContent.split("=");
						try{
							commandVars.put(varType[0],CommandVarType.valueOf(varType[1].toUpperCase()));
						}catch (Exception e){
							//TODO error not possible var type
						}
					}
				}else if(component.toLowerCase().matches("^function\\s*=\\s*\\{(.*)$")){
					functionContent=fillEmptyFunctions(component.replaceAll("function\\s*=\\s*\\{(.*)}$","$1"));
				}
			}
			plugin.addCommand(prompt,commandVars,functionContent);
		}
	}

	private void getObjects(Plugin plugin, String code) {
		List<String> objectsText=new ArrayList<>();
		getElementsWithCodeInKeys(objectsText,code,false);
		for(String objectText:objectsText){
			if(objectText.isEmpty())continue;
			String objectName=objectText.replaceAll("^([^{]+)\\{(.+)$","$1");
			if(objectName.matches("^([A-Za-z0-9_]+)\\s+from\\s+([A-Za-z0-9_]+)\\s*$")){
				objectName=objectName.replaceAll("^([A-Za-z0-9_]+)\\s+from\\s+(.+)$","$1");
			}
			final String objectNameFinal=objectName;
			if(plugin.getObjects().stream().anyMatch(obj->obj.getName().equals(objectNameFinal))){
				//TODO error same name as existing object
				continue;
			}
			PluginObject object=new PluginObject(plugin,objectName);
			if(objectName.equalsIgnoreCase("Plugin"))object=plugin.getMainObject();
			String objectContent=objectText.replaceAll("^([^{]+)\\{(.*)}$","$2");
			boolean error=false;
			for(String content:mainPlugin.getCodeExecuter().getInstructionsFromFunction("{"+objectContent+"}")){
				if(content.matches("^([A-Za-z0-9_]+)$")||content.matches("^([A-Za-z0-9_]+)=(.*)$")){
					String propertyName=content.replaceAll("^([A-Za-z0-9_]+)=(.*)$","$1").trim();
					if(!ErrorManager.checkTextVariable(propertyName,propertyName,mainPlugin)){error=true;break;}
					if(content.matches("^([A-Za-z0-9_]+)\\s*=\\s*(.*)$")){
						String propertyEqual=content.replaceAll("^([A-Za-z0-9_]+)\\s*=\\s*(.*)$","$2");
						Map<String,Object> vars=mainPlugin.getPluginVars(plugin);
						mainPlugin.getCodeExecuter().executeInstruction(propertyEqual,content,vars);
						if(PluginCoder.isErrorFound()){error=true;break;}
						object.addProperty(propertyName,propertyEqual);
					}else object.addProperty(propertyName,null);
				}else {
					content=fillEmptyFunctions(content);
					if(content.toLowerCase().matches("^"+objectName.toLowerCase()+"\\s*(\\((.*)\\))?\\s*\\{(.*)}$"))object.getConstructors().add(content);
					else object.getDeclaredFunctions().add(content);
				}
			}
			if(!error&&!objectName.equalsIgnoreCase("Plugin"))plugin.getObjects().add(object);
			plugin.updateMainObjectInstance();
		}
		//registrar herencia
		for(String objectText:objectsText){
			if(objectText.isEmpty())continue;
			String objectName=objectText.replaceAll("^([^{]+)\\{(.+)$","$1");
			String parent="";
			if(!objectName.matches("^([A-Za-z0-9_]+)\\s+from\\s+(.+)$"))continue;
			parent=objectName.replaceAll("^([A-Za-z0-9_]+)\\s+from\\s+([A-Za-z0-9_]+)\\s*$","$2");
			objectName=objectName.replaceAll("^([A-Za-z0-9_]+)\\s+from\\s+(.+)$","$1");
			PluginObject object=plugin.getObject(objectName);
			if(objectName.equalsIgnoreCase("Plugin"))object=plugin.getMainObject();
			PluginObject parentObj=plugin.getObject(parent);
			if(parentObj!=null)object.setParent(parentObj);
		}
	}
	private void getListenerFunctions(List<String>codeList,String code) {
		getElementsWithCodeInKeys(codeList,code,true);
	}
	private void getElementsWithCodeInKeys(List<String>container,String code,boolean fillEmptyFunctions) {
		String element="";
		int keys=0;
		boolean firstKey=false;
		for(Character c:code.toCharArray()) {
			element+=String.valueOf(c);
			if(c.equals('{')) {
				keys++;firstKey=true;
			}
			else if(c.equals('}'))keys--;
			if(keys==0&&firstKey) {
				if(fillEmptyFunctions)element=fillEmptyFunctions(element);//poner llaves a los condicionales y bucles sin llaves
				container.add(element);
				firstKey=false;
				element="";
			}
		}
	}

	private String fillEmptyFunctions(String code) {
		String function="";
		String[] arrayInst=code.split(";");
		for(int i=0;i<arrayInst.length;i++) {
			String inst=arrayInst[i];
			inst=inst.replaceAll("^\\s*", "");
			if(inst.contains("if")) {
				String paramsIf=transformToRegrexString(getParamsInFunction(inst,"if"));
				inst=inst.replaceAll("if\\("+paramsIf+"\\)(?!\\{)(.+)", "if("+paramsIf+"){$1;}");
			}
			inst=inst.replaceAll("else (?!if)(?!\\{)(.+)", "else{$1;}");
			if(inst.contains("for")) {
				String paramsFor=transformToRegrexString(getParamsInFunction(inst,"for"));
				inst=inst.replaceAll("for\\("+paramsFor+"\\)(?!\\{)(.+)", "for("+paramsFor+"){$1;}");
			}
			if(inst.contains("while")) {
				String paramsWhile=transformToRegrexString(getParamsInFunction(inst,"while"));
				inst=inst.replaceAll("while\\("+paramsWhile+"\\)(?!\\{)(.+)", "while("+paramsWhile+"){$1;}");
			}
			if(inst.contains("delay")) {
				String paramsDelay=transformToRegrexString(getParamsInFunction(inst,"delay"));
				inst=inst.replaceAll("delay\\("+paramsDelay+"\\)(?!\\{)(.+)", "delay("+paramsDelay+"){$1;}");
			}
			if(inst.contains("repeat")) {
				String paramsrepeat=transformToRegrexString(getParamsInFunction(inst,"repeat"));
				inst=inst.replaceAll("repeat\\("+paramsrepeat+"\\)(?!\\{)(.+)", "repeat("+paramsrepeat+"){$1;}");
			}
			function+=inst.endsWith("}")?(inst.matches("^([^{=]+)\\s*=\\s*\\{(.*)}$")?inst+";":inst):inst+";";
		}
	    return function;
	}

	private String transformToRegrexString(String paramsFor) {
		paramsFor=paramsFor.replace("(","\\(");
		paramsFor=paramsFor.replace(")","\\)");
		paramsFor=paramsFor.replace(".","\\.");
		return paramsFor;
	}

	public String getParamsInFunction(String instruction,String start) {
		int index=instruction.indexOf(start);
		if(instruction.charAt(index+start.length())=='('&&instruction.charAt(index+1+start.length())==')')return "";
		String paramsString="";
		String stringIterated="";
		int parentesisCount=0;
		boolean parentesisFound=false;
		boolean startFound=false;
		for(Character c:instruction.toCharArray()) {
			if(c.equals('(')) {
				parentesisCount++;parentesisFound=true;
				if(parentesisCount==1)continue;
			}
			else if(c.equals(')')) {
				parentesisCount--;
				if(start.isEmpty()) {
					if(parentesisCount==0&&parentesisFound)break;
				}else {
					if(parentesisCount==0&&parentesisFound&&startFound)break;
				}
			}
			if(start.isEmpty()) {
				if(parentesisFound)paramsString+=String.valueOf(c);
			}
			else {
				if(stringIterated.endsWith(start)) {
					if(parentesisFound)paramsString+=String.valueOf(c);
					startFound=true;
				}
				else stringIterated+=String.valueOf(c);
			}
		}
		return paramsString;
	}
	public void readLanguage(){
		Language language=mainPlugin.getLanguage();
		int languageIndex=1;
		if(language==Language.SPANISH)languageIndex=2;
		else if(language==Language.PORTUGUESE)languageIndex=3;
		else if(language==Language.FRENCH)languageIndex=4;
		else if(language==Language.GERMAN)languageIndex=5;
		else if(language==Language.ITALIAN)languageIndex=6;
		else if(language==Language.RUSSIAN)languageIndex=7;
		fillTranslationMap("guiTranslations",languageIndex,PluginCoder.getCoderGUI().getGuiTranslations());
		fillTranslationMap("errorTranslations",languageIndex, ErrorManager.getErrorTranslation());
	}
	private void fillTranslationMap(String file,int languageIndex,Map<String,String> translations){
		translations.clear();
		boolean firstRow=false;
		for(String line:getLinesFromInternalFile("messages/"+file+".csv")){
			if(!firstRow){firstRow=true;continue;}
			String[] fields = line.split(";");
			if(fields.length==0)continue;
			translations.put(fields[0],fields[languageIndex]);
		}
	}
	public static List<String> getLinesFromInternalFile(String path){
		InputStream in = PluginFileReader.class.getClassLoader().getResourceAsStream(path);
		if (in == null)return new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			return reader.lines().collect(Collectors.toList());
		}catch (IOException e) {return new ArrayList<>();}
	}
}
