package berty.plugincoder.interpreter.executer;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


import java.util.*;
import java.util.stream.Collectors;

import berty.plugincoder.utils.CodeUtils;
import berty.plugincoder.interpreter.classes.scoreboard.Scoreboard;
import berty.plugincoder.interpreter.loops.PluginFor;
import berty.plugincoder.interpreter.loops.PluginWhile;
import berty.plugincoder.interpreter.objects.PluginMethod;
import berty.plugincoder.interpreter.objects.PluginObject;
import berty.plugincoder.interpreter.objects.PluginObjectInstance;
import berty.plugincoder.interpreter.plugin.Plugin;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import berty.plugincoder.interpreter.error.ErrorManager;

public class CodeExecuter {

	private PluginCoder mainPlugin;
	private List<String> customMethods;
	public CodeExecuter(PluginCoder plugin) {
		this.mainPlugin =plugin;
		customMethods=Arrays.asList("teleport()","message()","scoreboard");
	}
	public Object executeFunction(String function,String originalInstruction,Map<String,Object> variables) {
		PluginCoder.setErrorFound(false);
		Object resultado=Void.class;
		for(String instruction:getInstructionsFromFunction(function)) {
			if(PluginFor.stopLoop||PluginWhile.stopLoop||PluginFor.continueLoop||PluginWhile.continueLoop)return resultado;
			String type=getInstructionType(instruction,variables);
			if(type.equals("Variable")) {
				this.executeVariable(instruction,variables);
			}else if(type.equals("Conditional")){
				Map<String,Object> variablesClone=new HashMap<>(variables);
				resultado= mainPlugin.getConditionals().executeConditional(instruction,variables);
				new HashMap<>(variables).keySet().stream().filter(k->!variablesClone.containsKey(k)).forEach(k->variables.remove(k));
			}else if(type.equals("Return")) {
				return this.executeInstruction(instruction.replaceAll("^return\\s*", ""),
						originalInstruction.isEmpty()?instruction:originalInstruction, variables);
			}else if(type.equals("For")) {
				resultado= mainPlugin.getForManager().executeFor(instruction,
						originalInstruction.isEmpty()?instruction:originalInstruction, variables);
			}else if(type.equals("While")) {
				Map<String,Object> variablesClone=new HashMap<>(variables);
				resultado= mainPlugin.getWhileManager().executeWhile(instruction,
						originalInstruction.isEmpty()?instruction:originalInstruction, variables);
				new HashMap<>(variables).keySet().stream().filter(k->!variablesClone.containsKey(k)).forEach(k->variables.remove(k));
			}else if(type.equals("Delay")) {
				mainPlugin.getDelay().executeDelay(instruction,originalInstruction.isEmpty()?instruction:originalInstruction, variables);
			}else if(type.equals("Repeat")) {
				List<String> tasks=variables.keySet().stream().filter(v->{
					if(!v.startsWith("task"))return false;
					String taskIDString=v.replace("task", "");
					try {
						int taskID=Integer.parseInt(taskIDString);
						if(mainPlugin.getRepeat().getTaskIDs().contains(taskID))return true;
						return false;
					}catch (Exception e) {
						return false;
					}
				}).collect(Collectors.toList());
				if(!tasks.isEmpty()) {
					ErrorManager.repeatInsideOfRepeat(originalInstruction);
					return Void.class;
				}
				resultado= mainPlugin.getRepeat().executeRepeat(instruction,originalInstruction.isEmpty()?instruction:originalInstruction, variables);
			}else if(type.equals("Cancel")) {
				mainPlugin.getRepeat().executeCancelTask(instruction,originalInstruction.isEmpty()?instruction:originalInstruction, variables);
			}else{
				if(PluginFor.active||PluginWhile.active){
					String trimInst=instruction.trim();
					if(trimInst.equals("continue")){
						if(PluginFor.active)PluginFor.continueLoop=true;
						else PluginWhile.continueLoop=true;
						return Void.class;
					}
					else if(trimInst.equals("stop")){
						if(PluginFor.active)PluginFor.stopLoop=true;
						else PluginWhile.stopLoop=true;
						return Void.class;
					}
				}
				//ejecucion normal de una instruccion
				this.executeInstruction(instruction,originalInstruction.isEmpty()?instruction:originalInstruction,variables);
			}
			if(PluginCoder.isErrorFound())return Void.class;
			else if(resultado==null||!resultado.equals(Void.class))return resultado;
		}
		return resultado;
	}
	public List<String> getInstructionsFromFunction(String function) {
		List<String> instructions=new ArrayList<>();
		boolean isFunction=false;
		int llavesCount=0;
		String instruction="";
		int charIndex=0;
		for(Character c:function.replaceAll("^([^{]*)\\{(.*)}$","$2").toCharArray()) {
			if(instruction.startsWith("else")) {
				if(instructions.size()>0){
					String conditional=instructions.get(instructions.size()-1);
					if(conditional.matches("^if\\s*\\((.+)$")||conditional.matches("^else if\\s*\\((.+)$")) {
						instruction=conditional+instruction;
						instructions.remove(instructions.size()-1);
					}
				}
				isFunction=true;
			}
			if(isFunction) {
				instruction+=String.valueOf(c);
			}else if(c.equals(';')) {
				instructions.add(instruction);
				instruction="";charIndex=0;continue;
			}else instruction+=String.valueOf(c);

			if(c.equals('{')&&!instruction.substring(0,charIndex).matches("([^{]+)\\s*=\\s*$")) {
				isFunction=true;llavesCount++;
			}else if(c.equals('}')&&isFunction) {
				llavesCount--;
				if(llavesCount==0) {
					isFunction=false;
					instructions.add(instruction);
					instruction="";
					charIndex=0;continue;
				}
			}
			charIndex++;
		}
		return instructions;
	}
	public List<String> getGUIInstructionsFromFunction(String function){
		List<String> instructions=new ArrayList<>();
		for(String instruction:getInstructionsFromFunction(function)){
			if(instruction.matches("^if\\s*\\((.+)$")||instruction.matches("^else if\\s*\\((.+)$")){
				String subConditional="";
				int keysCount=0;
				for(Character c:instruction.toCharArray()){
					subConditional+=c;
					if(c.equals('{'))keysCount++;
					else if(c.equals('}')){
						keysCount--;
						if(keysCount==0){
							instructions.add(subConditional);
							subConditional="";
						}
					}
				}
			}else instructions.add(instruction);
		}
		return instructions;
	}
	private void executeVariable(String instruction,Map<String,Object> variables) {
		//hacer que se puedan ejecutar funciones
		String[] vars=instruction.split("=");
		if(!ErrorManager.checkTextVariable(vars[0], instruction, mainPlugin))return;
		String subInstruction="";
		for(int i=1;i<vars.length;i++) {
			if(i!=vars.length-1)subInstruction+=vars[i]+"=";
			else subInstruction+=vars[i];
		}
		subInstruction=subInstruction.trim();
		String type=getVarInstructionType(subInstruction,variables);
		Object resultado = Void.class;
		if(type.equalsIgnoreCase("String"))resultado=this.executeString(subInstruction, instruction, variables);
		else if(type.equalsIgnoreCase("Math"))resultado= mainPlugin.getMath().executeMath(subInstruction,instruction,variables);
		else if(type.equalsIgnoreCase("Boolean"))resultado= mainPlugin.getLogic().executeBoolean(subInstruction,instruction, variables);
		else resultado=this.executeInstruction(subInstruction,instruction,variables);
		variables.put(vars[0], resultado);
	}
	public Object checkConstructor(String instruction,Map<String,Object> variables){
		String constructorName=instruction.replaceAll("^([a-zA-Z_]+)\\((.+)$","$1");
		List<String> params=getStringParameters(instruction);
		Class objectClass= mainPlugin.getConstructorTranslator().get(constructorName);
		if(objectClass!=null){
			List<Object> parameters=getParameters(params,instruction,variables);
			List<Class> paramsClasses= parameters.stream().map(object->object==null?null:object.getClass()).collect(Collectors.toList());
			if(constructorName.equals("Inventory")){
				if(paramsClasses.size()==2){
					if(!paramsClasses.get(0).getTypeName().equals(String.class.getTypeName()))return Void.class;
					if(!paramsClasses.get(1).getTypeName().equals(int.class.getTypeName())
							&&!paramsClasses.get(1).getTypeName().equals(Integer.class.getTypeName()))return Void.class;
					return Bukkit.createInventory(null,(int)parameters.get(1)*9,(String)parameters.get(0));
				}else if(paramsClasses.size()==1){
					if(!paramsClasses.get(0).getTypeName().equals(int.class.getTypeName())
							&&!paramsClasses.get(0).getTypeName().equals(Integer.class.getTypeName()))return Void.class;
					return Bukkit.createInventory(null,(int)parameters.get(0)*9);
				}
				return Void.class;
			}
			for(Constructor constructor:objectClass.getConstructors()){
				if(constructor.getParameterTypes().length!=paramsClasses.size())continue;
				boolean next=false;
				for(int i=0;i<constructor.getParameterTypes().length;i++){
					Class constParamClass=constructor.getParameterTypes()[i];
					Class paramClass=paramsClasses.get(i);
					if(paramClass==null||constParamClass.equals(paramClass)||constParamClass.isAssignableFrom(paramClass)
					||CodeUtils.primitiveToWrapper(constParamClass).equals(CodeUtils.primitiveToWrapper(paramClass)))continue;
					try{
						if(constParamClass.isEnum()){
							Object enumValue=constParamClass.getMethod("valueOf", String.class).invoke(null, parameters.get(i).toString());
							parameters.set(i,enumValue);continue;
						}
					}catch (Exception e){}
					next=true;break;
				}
				if(next)continue;
				try{
					return constructor.newInstance(parameters.toArray());
				}catch (Exception e){e.printStackTrace();}
			}
			return Void.class;
		}//constructor de pluginObject
		Plugin plugin;
		if(variables.containsKey("plugin"))plugin=((PluginObjectInstance)variables.get("plugin")).getBaseObject().getPlugin();
		else plugin=((PluginObjectInstance)variables.get("this")).getBaseObject().getPlugin();
		for(PluginObject object:plugin.getObjects()){
			if(!constructorName.equals(object.getName()))continue;
			PluginObjectInstance newObject=object.getInstance();
			if(object.getConstructors().isEmpty()&&params.isEmpty())return newObject;
			for(String constructor:object.getConstructors()){
				List<String> constructorParams=getStringParameters(constructor.replaceAll("^([^{]+)\\{(.*)}$","$1"));
				if(params.size()!=constructorParams.size())continue;
				executeCustomFunction(newObject,constructor,variables,constructorParams,params);
				return newObject;
			}
		}
		return Void.class;
	}
	public Object executeInstruction(String instruction, String originalInstruction, Map<String, Object> variables) {
		if(instruction.isEmpty())return Void.class;
		if(variables.containsKey(instruction))return variables.get(instruction);
		Object numero= mainPlugin.getMath().checkDoubleLong(instruction);
		if(numero!=null)return numero;
		if(instruction.equalsIgnoreCase("true"))return true;
		if(instruction.equalsIgnoreCase("false"))return false;
		if(instruction.equalsIgnoreCase("null"))return null;
		if(instruction.startsWith("[")&&instruction.endsWith("]")) {
			return mainPlugin.getContainers().getListFromInstruction(instruction, originalInstruction, variables);
		}if(instruction.startsWith("{")&&instruction.endsWith("}")) {
			return mainPlugin.getContainers().getMapFromInstruction(instruction, originalInstruction, variables);
		}
		if(instruction.matches("^([a-zA-Z_]+)\\((.+)$")||instruction.matches("^([a-zA-Z_]+)$")){
			Object constructorObject=checkConstructor(instruction,variables);
			if(!constructorObject.equals(Void.class))return constructorObject;
		}
		if(mainPlugin.getColorTranslator().containsKey(instruction)){
			return ChatColor.translateAlternateColorCodes('&',mainPlugin.getColorTranslator().get(instruction));
		}
		String[] methods=getMethodsOfInstruction(instruction);
		Object variable=variables.get(methods[0]);
		if(variables.containsKey(methods[0])){
			if(variable==null&&methods.length>1){
				ErrorManager.nullVariable(methods[0],originalInstruction);
				return Void.class;
			}
		}else{
			if(!(methods[0].matches("^([a-zA-Z0-9_]+)$")&&methods.length>1))return instruction;
			List<String> methodList=new ArrayList<>(Arrays.asList(methods));methodList.remove(0);
			PluginObjectInstance plugin= (PluginObjectInstance) variables.get("plugin");
			if(methodList.stream().anyMatch(method->{
				String methodName=method.replaceAll("^([^(]+)\\((.+)\\)$","$1");
				if(methodName.length()!=method.length())methodName+="()";
				if(mainPlugin.getMethod(methodName)!=null)return false;
				if(plugin.getBaseObject().getPlugin().getObjects().stream().anyMatch(object->
					object.getDeclaredFunctions().stream().anyMatch(function->function.trim().startsWith(method.trim()))
				))return false;
				return true;
			}))return instruction;
			ErrorManager.varNotExists(methods[0],instruction);
			return Void.class;
		}
		String nextMethod="";
		String executedCode=methods[0];
		for(int index=1;index<methods.length;index++) {
			nextMethod=methods[index];
			try {
				if(!ErrorManager.checkExecutedVariable(variable, originalInstruction, executedCode,nextMethod))return Void.class;
				Class<?> clase = variable.getClass();
				if(clase.equals(PluginObjectInstance.class)){//la variable es un objeto creado por el plugin
					PluginObjectInstance object=((PluginObjectInstance) variable);
					String[] nameParams=methods[index].replaceAll("^([^(]+)\\((.+)\\)$","$1|$2").split("\\|");
					String name=nameParams[0];
					if(object.getProperties().keySet().contains(name)){//se ejecuta una propiedad get o set
						if(methods[index].length()==name.length()){//se ejecuta un get
							variable=object.getProperties().get(name);
						}else {//se ejecuta un set
							String param=nameParams[1];
							Object paramResult=executeInstruction(param,originalInstruction,variables);
							if(Void.class.equals(paramResult)){
								ErrorManager.settingVoidValueInObjectProperty(name,originalInstruction);
								return Void.class;
							}
							object.getProperties().put(name,paramResult);
							variable=Void.class;
						}
					}else {//se ejecuta una funcion del objeto
						Object res=executeObjectFunction(object,methods[index],originalInstruction,variables,executedCode);
						if(PluginCoder.isErrorFound())return Void.class;
						variable=res;
					}
				}else{ //objeto existente en java
					String methodName=methods[index].replaceAll("^([^(]+)\\((.+)\\)$","$1");
					List<String> paramsString=getStringParameters(methods[index]);
					if(methodName.length()!=methods[index].length())methodName+="()";
					List<Object> parametros=getParameters(paramsString,originalInstruction,variables);
					Method metodo=getMethod(clase, methodName,parametros,true);
					if(metodo==null) {
						ErrorManager.methodNotFound(methodName,originalInstruction,executedCode);
						return Void.class;
					}
					if(!customMethods.contains(methodName))variable=executeMethod(variable,metodo,parametros,methodName,executedCode+"."+methods[index],originalInstruction);
					else variable=executeCustomMethod(variable,metodo,parametros,methodName,executedCode+"."+methods[index],originalInstruction);
				}
				executedCode+="."+methods[index];
			}catch (Exception e) {e.printStackTrace();}
		}
		return variable;
		
	}
	private Object executeObjectFunction(PluginObjectInstance object, String functionMethod, String originalInstruction,
										 Map<String,Object> variables,String executedCode) {
		String instructionFunctionName=functionMethod.replaceAll("^([^(]+)\\((.*)$","$1");
		List<String> instructionParams=getStringParameters(functionMethod);
		List<String> functionCandidates=object.getBaseObject().getFunctions().stream().filter(function->{
			String functionContent=function.replaceAll("^([^{;]+)\\{(.+)$","$1");
			String functionName=functionContent.replaceAll("^([^(]+)\\((.*)$","$1");
			if(!functionName.equals(instructionFunctionName))return false;
			return true;
		}).collect(Collectors.toList());
		if(functionCandidates.isEmpty()){
			ErrorManager.methodNotFound(functionMethod,originalInstruction,executedCode);
			return Void.class;
		}
		Optional<String> foundFunction=functionCandidates.stream().filter(function->{
			String functionContent=function.replaceAll("^([^{;]+)\\{(.+)$","$1");
			List<String> functionParams=getStringParameters(functionContent);
			if(instructionParams.size()!=functionParams.size())return false;
			return true;
		}).findFirst();
		if(!foundFunction.isPresent()){
			ErrorManager.wrongNumParamsInFunction(instructionFunctionName,instructionParams.size(),originalInstruction);
			return Void.class;
		}
		List<String> functionParams=getStringParameters(foundFunction.get().replaceAll("^([^{;]+)\\{(.+)$","$1"));
		return executeCustomFunction(object,foundFunction.get(),variables,functionParams,instructionParams);
	}
	private Object executeCustomFunction(PluginObjectInstance object,String function,Map<String,Object> variables,List<String> functionParams,List<String> instructionParams){
		Map<String,Object> variableParams=mainPlugin.getPluginVars(object.getBaseObject().getPlugin());
		variableParams.putAll(object.getProperties());
		if(object.getBaseObject().getName().equals(object.getBaseObject().getPlugin().getMainObject().getName()))variableParams.remove("plugin");
		variableParams.put("this", object);
		for(String property:object.getProperties().keySet())
			variableParams.put(property,object.getProperties().get(property));
		for(int i=0;i<functionParams.size();i++){
			String param=functionParams.get(i);
			Object value=variables.get(instructionParams.get(i));
			if(!variables.containsKey(instructionParams.get(i)))value=instructionParams.get(i);
			variableParams.put(param,value);
		}
		//ejecutar la funcion con las variables de los parametros y las propiedades del objeto
		Object resultado= executeFunction(function,"",variableParams);
		if(PluginCoder.isErrorFound())return Void.class;
		Set<String>properties=object.getProperties().keySet();
		for(String variable:variableParams.keySet()){
			if(properties.contains(variable)&&!functionParams.contains(variable))object.getProperties().put(variable,variableParams.get(variable));
		}
		return resultado;
	}
	public Object executeString(String instruction, String originalInstruction, Map<String, Object> variables) {
		String text="";
		String textToProcess="";
		int parentesisCount=0;
		for(Character character:instruction.toCharArray()) {
			if(character.equals('('))parentesisCount++;
			else if(character.equals(')'))parentesisCount--;
			else if(!character.equals('+')||parentesisCount!=0) {
				textToProcess+=String.valueOf(character);continue;
			}
			if(mainPlugin.getColorTranslator().get(textToProcess)!=null)textToProcess= mainPlugin.getColorTranslator().get(textToProcess);
			else if(textToProcess.matches("^#[A-Fa-f0-9]{6}$")){
				String hexColor="§x";
				for(Character hexChar:textToProcess.substring(1).toCharArray())hexColor+="§"+hexChar;
				textToProcess=hexColor;
			}
			text=getStringFromTextVar(textToProcess,text,instruction,variables);
			if(text==null)return Void.class;
			textToProcess="";
		}
		text=getStringFromTextVar(textToProcess,text,originalInstruction,variables);
		if(text==null||PluginCoder.isErrorFound())return Void.class;
		return text;
	}
	private String getStringFromTextVar(String textToProcess,String text,String originalInstruction, Map<String, Object> variables) {
		String parsedString="";
		for(Character character:textToProcess.toCharArray()){
			if(character.equals('(')){
				String execution=parsedString.replaceAll("([^.(\\s]+)\\.(.*)$","$1.$2");
				if(!isExecution(execution,variables.keySet()))continue;
			}else if(character.equals(')')){
				String execution=parsedString.replaceAll("([^.(]+)\\.([^.\\s]*)\\((.*)$","$1.$2($3)");
				if(!isExecution(execution,variables.keySet()))continue;
			}
			parsedString+=character;
		}
		textToProcess=parsedString;
		Object textResult=this.executeInstruction(textToProcess, originalInstruction, variables);
		if(textResult==null||textResult.getClass().equals(Void.class)) {
			ErrorManager.isNotString(textToProcess,originalInstruction);
			return null;
		}
		return text+textResult;
	}
	public Method getMethod(Class<?>clase, String methodName, List<Object> params,boolean isExecutingCode) {
		checkCustomClasses(methodName,params,isExecutingCode);
		try{
			Method method=getMethodsOfJavaType(clase,methodName).stream()
					.filter(meth ->validateParams(meth.getParameterTypes(), params,isExecutingCode))
					.sorted(Comparator.comparing(meth->Arrays.stream(meth.getParameterTypes()).mapToInt(param->param.getTypeName().length()).sum()))
					.findFirst().orElse(null);
			if(method==null){
				if(methodName.equals("teleport()"))return clase.getMethod("teleport", Location.class);
				else if(methodName.equals("message()")){
					if(params.size()==1)return clase.getMethod("sendMessage",String.class);
					return clase.getMethod("sendMessage",String[].class);
				}
			}
			return method;
		}catch (Exception e){return null;}
	}
	private List<Method> getMethodsOfJavaType(Class<?>clase, String methodName){
		List<Method> methods=new ArrayList<>();
		PluginMethod pluginMethod=mainPlugin.getMethod(methodName);
		if(pluginMethod==null)return methods;
		methods=Arrays.stream(clase.getMethods()).filter(method-> pluginMethod.getTranslatedMethodClasses().get(method.getName())!=null&&
				pluginMethod.getTranslatedMethodClasses().get(method.getName())
	   .stream().anyMatch(classType-> {
		   try {return Class.forName(classType).isAssignableFrom(clase);}
		   catch (ClassNotFoundException e) {return false;}
		})).collect(Collectors.toList());
		return methods;
	}
	//isExecutingCode se usa para saber si se busca un metodo basado en parametros en ejecucion o en parametros dado el tipo (JavaTranslator)
	public boolean validateParams(Class[] parameterTypes, List<Object> params, boolean isExecutingCode) {
		List<Class> methodParameterTypes=new ArrayList<>();
		for(int index=0;index<parameterTypes.length;index++){
			Class methodParam=parameterTypes[index];
			if(methodParam.isArray()&&methodParam.isArray()&&params.subList(index,params.size()).stream().allMatch(param->
					CodeUtils.primitiveToWrapper(param.getClass()).equals(CodeUtils.primitiveToWrapper(methodParam.getComponentType())))){
				for(int paramIndex=index;paramIndex<params.size();paramIndex++)methodParameterTypes.add(methodParam.getComponentType());
			}else methodParameterTypes.add(methodParam);
		}
		if(methodParameterTypes.size()!=params.size())return false;
		for(int index=0;index<methodParameterTypes.size();index++){
			if(!validateGenericParams(methodParameterTypes.get(index),params,isExecutingCode,index))return false;
		}
		return true;
	}
	private boolean validateGenericParams(Class paramClass,List<Object> params,boolean isExecutingCode,int index){
		try{
			if(paramClass.getTypeName().equals(Object.class.getTypeName()))return true;
			paramClass=CodeUtils.primitiveToWrapper(paramClass);
			Object param=params.get(index);
			if(param==null)return !typeIsMath(paramClass.getTypeName())&&!paramClass.getTypeName().equals(boolean.class.getTypeName());
			String paramType=isExecutingCode?param.getClass().getTypeName():param.toString();
			Class primitiveClass= CodeUtils.getPrimitiveType(paramType);
			Class entryParamClass= CodeUtils.primitiveToWrapper(primitiveClass==null?Class.forName(paramType):primitiveClass);
			//pasar de un numero a otro cuando sea necesario
			if(Number.class.isAssignableFrom(paramClass)&&Number.class.isAssignableFrom(entryParamClass)&&isExecutingCode){
				try{
					Method valueOf = paramClass.getMethod("valueOf", String.class);
					Object newNumber=valueOf.invoke(null,param.toString());
					params.set(index,newNumber);entryParamClass=paramClass;
				}catch (Exception e){}
			}
			if(paramClass.isEnum()&&entryParamClass.getTypeName().equals(String.class.getTypeName())){
				if(!isExecutingCode)return true;
				try{
					Object enumValue=paramClass.getMethod("valueOf", String.class).invoke(null, param.toString());
					params.set(index,enumValue);
				}catch (Exception e){return false;}
			}
			if(paramClass.getTypeName().equals(entryParamClass.getTypeName()))return true;
			if(paramClass.isAssignableFrom(entryParamClass))return true;
			return false;
		}catch (Exception e){e.printStackTrace();return false;}
	}
	private void checkCustomClasses(String method,List<Object> parametros,boolean isExecutingCode){
		if(method.equals("scoreboard()")){
			if(isExecutingCode){
				if(parametros.get(0)!=null&&parametros.get(0).getClass().getTypeName().equals(Scoreboard.class.getTypeName())){
					parametros.set(0,((Scoreboard)parametros.get(0)).getBukkitScoreboard());
				}
			}else parametros.set(0,"org.bukkit.scoreboard.Scoreboard");
		}
	}
	public boolean typeIsMath(String type){
		try {
			Class classType=Class.forName(type);
			return classType.isPrimitive() && classType != boolean.class && classType!= char.class
					|| Number.class.isAssignableFrom(classType);
		}catch (Exception e){return false;}
	}
	private Object executeMethod(Object var, Method metodo, List<Object> parametros, String methodName, String instruction, String originalInstruction) {
		try {
			if(metodo.getParameterTypes().length>0&&metodo.getParameterTypes()[metodo.getParameterTypes().length-1].isArray()){
				Class arrayClass=metodo.getParameterTypes()[metodo.getParameterTypes().length-1].getComponentType();
				int arrayLength=parametros.size()-metodo.getParameterTypes().length+1;
				Object array = Array.newInstance(arrayClass,arrayLength);
				for(int paramIndex=0;paramIndex<arrayLength;paramIndex++){
					Array.set(array, paramIndex, parametros.get(metodo.getParameterTypes().length-1));
					parametros.remove(metodo.getParameterTypes().length-1);
				}
				parametros.add(array);
			}
			Object result=metodo.invoke(var,parametros.toArray());
			if(metodo.getReturnType().getTypeName().equals("void"))return Void.class;
			return result;
		}catch (Exception e) {
			ErrorManager.methodExecutionFailed(methodName,instruction,originalInstruction);
			e.printStackTrace();
			return Void.class;
		}
	}
	private Object executeCustomMethod(Object var, Method metodo, List<Object> parametros, String methodName,String instruction, String originalInstruction) {
		try {
			Object result=Void.class;
			List<String> paramsType=new ArrayList<>();
			if(methodName.equalsIgnoreCase("teleport()")) {
				if(parametros.size()==1) result=executeMethod(var,metodo,parametros, methodName, instruction, originalInstruction);
				else if(parametros.size()==3) {
					Entity entity=(Entity) var;
					paramsType.add("double");paramsType.add("double");paramsType.add("double");
					List<Object> newParams=parseNumParams(parametros,paramsType);
					Location l=new Location(entity.getWorld(),(double)newParams.get(0),(double)newParams.get(1),
							(double)newParams.get(2));
					result=executeMethod(var,metodo,Arrays.asList(l), methodName, instruction, originalInstruction);
				}else if(parametros.size()==4) {
					Entity entity=(Entity) var;
					paramsType.add(World.class.getTypeName());paramsType.add("double");paramsType.add("double");paramsType.add("double");
					List<Object> newParams=parseNumParams(parametros,paramsType);
					Location l=new Location((World)newParams.get(0),(double)newParams.get(1),(double)newParams.get(2),
							(double)newParams.get(3),entity.getLocation().getYaw(),entity.getLocation().getPitch());
					result=executeMethod(var,metodo,Arrays.asList(l), methodName, instruction, originalInstruction);
				}else if(parametros.size()==5) {
					Entity entity=(Entity) var;
					paramsType.add("double");paramsType.add("double");paramsType.add("double");
					paramsType.add("float");paramsType.add("float");
					List<Object> newParams=parseNumParams(parametros,paramsType);
					Location l=new Location(entity.getWorld(),(double)newParams.get(0),(double)newParams.get(1),
							(double)newParams.get(2),(float)newParams.get(3),(float)newParams.get(4));
					result=executeMethod(var,metodo,Arrays.asList(l), methodName, instruction, originalInstruction);
				}else if(parametros.size()==6) {
					paramsType.add(World.class.getTypeName());paramsType.add("double");paramsType.add("double");paramsType.add("double");
					paramsType.add("float");paramsType.add("float");
					List<Object> newParams=parseNumParams(parametros,paramsType);
					Location l=new Location((World)newParams.get(0),(double)newParams.get(1),(double)newParams.get(2),
							(double)newParams.get(3),(float)newParams.get(4),(float)newParams.get(5));
					result=executeMethod(var,metodo,Arrays.asList(l), methodName, instruction, originalInstruction);
				}
			}else if(methodName.equalsIgnoreCase("message()")) {
				if(parametros.size()!=1) return Void.class;
				result=executeMethod(var,metodo,parametros.stream().map(param->param+"").collect(Collectors.toList()), methodName, instruction, originalInstruction);
			}else if(methodName.equals("scoreboard")) {
				org.bukkit.scoreboard.Scoreboard bukkitScoreboard=
						(org.bukkit.scoreboard.Scoreboard)executeMethod(var,metodo,Arrays.asList(), methodName, instruction, originalInstruction);
				result= Scoreboard.getScoreboard(bukkitScoreboard);
			}
			if(metodo.getReturnType().getTypeName().equals("void"))return Void.class;
			return result;
		}catch (Exception e) {
			e.printStackTrace();
			return Void.class;
		}
		
	}
	public boolean isExecution(String instruction,Set<String> variables){
		String[] methods= getMethodsOfInstruction(instruction);
		if(!variables.contains(methods[0]))return false;
		for(int i=1;i<methods.length;i++){
			String methodName=methods[i].replaceAll("^([^(]+)\\((.*)\\)$","$1");
			if(methodName.contains(" "))return false;
		}
		return true;
	}
	public boolean instructionIsFunction(String instruction){
		if(instruction.trim().isEmpty())return false;
		if(instruction.trim().startsWith("{"))return false;
		if(!instruction.contains("{")||!instruction.endsWith("}"))return false;
		if(instruction.trim().startsWith("return "))return false;
		if(instruction.matches("^\\s*([a-zA-Z0-9_]+)\\s*$"))return false;
		if(instruction.matches("^\\s*([a-zA-Z0-9_]+)\\s*=(.+)$"))return false;
		if(instruction.matches("^\\s*([a-zA-Z0-9_]+)\\.(.+)$"))return false;
		return true;
	}
	private List<Object> parseNumParams(List<Object> parametros,List<String> paramsType) {
		List<Object> newParams=new ArrayList<>();
		for(int i=0;i<paramsType.size();i++) {
			String numFormat=paramsType.get(i);
			if(numFormat.equals("long")) {
				newParams.add(Long.parseLong(parametros.get(i).toString().replaceAll("^([^.]+)(.*)$","$1")));
			}else if(numFormat.equals("int")) {
				newParams.add(Integer.parseInt(parametros.get(i).toString().replaceAll("^([^.]+)(.*)$","$1")));
			}else if(numFormat.equals("double")) {
				newParams.add(Double.parseDouble(parametros.get(i).toString()));
			}else if(numFormat.equals("float")) {
				newParams.add(Float.parseFloat(parametros.get(i).toString()));
			}else newParams.add(parametros.get(i));
		}
		return newParams;
	}
	public List<String> getStringParameters(String method) {
		List<String> params=new ArrayList<>();
		String paramsString=method.replaceAll("^([^(]*)\\((.*)\\)$","$2");
		if(paramsString.equals(method))return params;
		int parentesisCount=0;
		String param="";
		for(Character c:paramsString.toCharArray()){
			if(c.equals('(')||c.equals('{'))parentesisCount++;
			else if(c.equals(')')||c.equals('}'))parentesisCount--;
			else if(c.equals(',')&&parentesisCount==0){
				params.add(param);param="";continue;
			}
			param+=c;
		}
		if(!param.trim().isEmpty())params.add(param);
		return params;
	}
	public List<Object> getParameters(List<String> params,String instruction, Map<String, Object> variables) {
		List<Object> parametrosObject=new ArrayList<>();
		for(String param:params) {
			if(variables.get(param)!=null) {parametrosObject.add(variables.get(param));continue;}
			Object resultado= mainPlugin.getMath().checkDoubleLong(param);
			if(resultado!=null){parametrosObject.add(resultado);continue;}
			//ejecutar codigo dentro del parámetro
			String type=getVarInstructionType(param,variables);
			if(type.equalsIgnoreCase("Boolean")) parametrosObject.add(mainPlugin.getLogic().executeBoolean(param,instruction, variables));
			else if(type.equalsIgnoreCase("Math")) parametrosObject.add(mainPlugin.getMath().executeMath(param,instruction, variables));
			else if(type.equalsIgnoreCase("String"))parametrosObject.add(executeString(param, instruction, variables));
			else parametrosObject.add(executeInstruction(param,instruction,variables));
		}
		return parametrosObject;
	}
	private String getInstructionType(String instruction,Map<String,Object>variables) {
		//Cambiar cuando se añadan más tipos de instrucciones
		if(instruction.startsWith("if(")||instruction.startsWith("else "))return "Conditional";
		if(instruction.startsWith("return "))return "Return";
		if(instruction.startsWith("delay("))return "Delay";
		if(instruction.startsWith("repeat("))return "Repeat";
		if(instruction.startsWith("for("))return "For";
		if(instruction.startsWith("while("))return "While";
		if(instruction.startsWith("cancel"))return "Cancel";
		else {
			int parentesisCount=0;
			for(Character c:instruction.toCharArray()) {
				if(c.equals('('))parentesisCount++;
				else if(c.equals(')'))parentesisCount--;
				if(parentesisCount==0&&c.equals('='))return "Variable";
			}
		}
		return "Execution";
	}
	public String getVarInstructionType(String instruction,Map<String, Object> variables) {
		//Cambiar cuando se añadan más tipos de instrucciones
		Plugin plugin;
		if(variables.containsKey("plugin"))plugin=((PluginObjectInstance)variables.get("plugin")).getBaseObject().getPlugin();
		else plugin=((PluginObjectInstance)variables.get("this")).getBaseObject().getPlugin();
		String constructorName=instruction.replaceAll("^([a-zA-Z]+)\\((.+)$","$1");
		if(mainPlugin.getConstructorTranslator().containsKey(constructorName)
			|| plugin.getObject(constructorName)!=null)return "Constructor";
		if(mainPlugin.getMath().isMath(instruction,variables.keySet())) return "Math";
		if(mainPlugin.getLogic().isBoolean(instruction,variables.keySet()))return"Boolean";
		if(isString(instruction))return"String";
		return "Execution";
	}
	private boolean isString(String instruction) {
		String[] elements=getElements(instruction,new String[]{"+"});
		return elements.length>1;
	}
	public String[] getMethodsOfInstruction(String instruction){
		return mainPlugin.getCodeUtils().getElementsBySeparator(instruction,'.');
	}
	public String[] getElementsNotModifyingParentesis(String operacion,String[] operadores) {
		String instParentesisMod=operacion.replace(".(","|(");
		instParentesisMod=instParentesisMod.replace("(",".(");
		String[] elements=getElements(instParentesisMod, operadores);
		for(int i=0;i<elements.length;i++){
			String booleanInst=elements[i];
			booleanInst=booleanInst.replace(".(","(");
			booleanInst=booleanInst.replace("|(",".(");
			elements[i]=booleanInst;
		}
		return elements;
	}
	public String[] getElements(String operacion,String[] operadores) {
		List<String> operadoresList=Arrays.asList(operadores);
		boolean isBool=false;
		if(operadoresList.contains(" and ")|| operadoresList.contains(" or ")){
			isBool=true;
			for(int i=0;i<operadores.length;i++) {
				if(operadores[i].equals(" and ")) {
					operacion=operacion.replace(operadores[i], "|");
					operadores[i]="|";
				}else if(operadores[i].equals(" or ")) {
					operacion=operacion.replace(operadores[i], "¨");
					operadores[i]="¨";
				}
			}
			operadoresList=Arrays.asList(operadores);
		}
		List<String> elements=new ArrayList<>();
		boolean dotFound=false;
		int parentesisCount=0;
		boolean parentesisFound=false;
		String element="";
		for(Character c:operacion.toCharArray()){
			if(c.equals('.'))dotFound=true;
			if(dotFound){
				if(c.equals('(')){
					parentesisCount++;parentesisFound=true;
					element+=c;
				}
				else if(c.equals(')')){
					parentesisCount--;
					if(!parentesisFound){
						if(!operadoresList.contains(")"))element+=c;
					}else element+=c;
					if(parentesisCount<=0)dotFound=false;
				}else {
					if(!parentesisFound&&operadoresList.contains(String.valueOf(c))) {
						dotFound=false;elements.add(element);element="";
					}
					else element+=c;
				}
			}else{
				if(operadoresList.contains(String.valueOf(c))){
					if(!element.isEmpty())elements.add(element);
					element="";
				}else element+=c;
			}
		}
		if(!element.isEmpty())elements.add(element);
		String[] res=new String[elements.size()];
		for(int i=0;i<elements.size();i++){
			res[i]=elements.get(i);
			if(isBool) {
				res[i]=res[i].replace("|"," and ");
				res[i]=res[i].replace("¨"," or ");
			}
		}
		return res;
	}
}
