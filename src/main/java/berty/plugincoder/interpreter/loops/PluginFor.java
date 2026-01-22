package berty.plugincoder.interpreter.loops;

import java.util.*;
import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginFor {
	private PluginCoder plugin;
	public static boolean active=false;
	public static boolean stopLoop=false;
	public static boolean continueLoop=false;
	public PluginFor(PluginCoder pluginCoder) {
		plugin=pluginCoder;
	}
	public Object executeFor(String instruction,String originalInstruction,Map<String,Object>variables) {
		Object resultado=Void.class;
		String[] forParams=plugin.getReader().getParamsInFunction(instruction,"").split(":");
		String varName=forParams[0];
		if(variables.keySet().stream().anyMatch(v->v.equals(varName))) {
			ErrorManager.existingVariableAsForVariable(forParams[0],originalInstruction);
			return resultado;
		}
		if(forParams.length<2){
			ErrorManager.forWithOneParamOnly(forParams[0],originalInstruction);
			return resultado;
		}
		String nums=forParams[1];
		//for numerico
		if(nums.contains("->")||nums.contains("<-")) {
			boolean invertir=false;
			String[] numParams=nums.split("->");
			if(numParams.length==1) {
				numParams=nums.split("<-");invertir=true;
			}
			if(numParams.length==1) {
				ErrorManager.badSyntaxInForRange(nums, originalInstruction);
				return resultado;
			}
			double num1;double num2;double increment;
			num1=getNumber(numParams[0], originalInstruction, variables);
			if(checkNumberError(numParams[0], originalInstruction))return Void.class;
			num2=getNumber(numParams[1], originalInstruction, variables);
			if(checkNumberError(numParams[1], originalInstruction))return Void.class;
			if(forParams.length==3){
				increment=Math.abs(getNumber(forParams[2], originalInstruction, variables));
				if(checkNumberError(numParams[2], originalInstruction))return Void.class;
			}else increment=1.;
			if(num1>num2)increment*=-1;
			if(invertir) {
				increment*=-1;double d=num1;num1=num2;num2=d;
			}
			if(num1==num2)return resultado;
			return executeNumFor(num1,num2,increment,varName, instruction, originalInstruction, variables);
		}
		//for iterado
		Object iterado=plugin.getCodeExecuter().executeInstruction(forParams[1], originalInstruction, variables);
		if(!(iterado instanceof Iterable)) {
			ErrorManager.notIterableParam(forParams[1],originalInstruction);
			return resultado;
		}
		Iterator<Object> iterador = ((Collection)iterado).iterator();
		boolean forActivated=PluginFor.active;
		boolean whileActivated=PluginWhile.active;
		active=true;PluginWhile.active=false;
		while(iterador.hasNext()) {
			resultado=executeForLoop(varName,iterador.next(),instruction, originalInstruction, variables);
			if(PluginCoder.isErrorFound())return Void.class;
			if(continueLoop){continueLoop=false;continue;}
			if(stopLoop){stopLoop=false;break;}
			if(!resultado.equals(Void.class))break;
		}
		PluginFor.active=forActivated;
		PluginWhile.active=whileActivated;
		return resultado;
	}
	private boolean checkNumberError(String numParam,String originalInstruction){
		if(PluginCoder.isErrorFound()){
			ErrorManager.isNotNumber(numParam, originalInstruction);
			return true;
		}
		return false;
	}
	private double getNumber(String var,String originalInstruction,Map<String,Object>variables) {
		String type=plugin.getCodeExecuter().getVarInstructionType(var, variables);
		Object resA;
		if(type.equals("Math"))resA=plugin.getMath().executeMath(var, originalInstruction, variables);
		else resA=plugin.getCodeExecuter().executeInstruction(var, originalInstruction, variables);
		if(resA==null) {
			ErrorManager.nullVariable(var, originalInstruction);
			return 0.;
		}
		Object resultado=plugin.getMath().checkDoubleLong(resA.toString());
		if(resultado!=null)return Double.parseDouble(resA.toString());
		else{
			ErrorManager.isNotNumber(resA.toString(),originalInstruction);
			return 0.;
		}
	}
	private Object executeNumFor(double num1,double num2,double increment,
			String varName,String instruction,String originalInstruction,Map<String,Object>variables) {
		Object resultado=Void.class;
		boolean forActivated=PluginFor.active;
		boolean whileActivated=PluginWhile.active;
		active=true;PluginWhile.active=false;
		for(double n=num1;num1<=num2?(n<=num2):(n>=num2);n+=increment) {
			Object num;
			if(String.valueOf(n).endsWith(".0")) num=(int)n;
			else num=n;
			resultado=executeForLoop(varName,num, instruction, originalInstruction, variables);
			if(continueLoop){continueLoop=false;continue;}
			if(stopLoop){stopLoop=false;break;}
			if(PluginCoder.isErrorFound())return Void.class;
			if(!resultado.equals(Void.class))return resultado;
		}
		PluginFor.active=forActivated;
		PluginWhile.active=whileActivated;
		return resultado;
	}
	private Object executeForLoop(String varName,Object varValue,String instruction,String originalInstruction,Map<String,Object>variables) {
		Map<String,Object> repeatVars=new HashMap<>(variables);
		variables.put(varName, varValue);
		Object resultado=plugin.getCodeExecuter().executeFunction(instruction, originalInstruction, variables);
		new HashMap<>(variables).keySet().stream().filter(k->!repeatVars.containsKey(k)).forEach(k->variables.remove(k));
		return resultado;
	}
}
