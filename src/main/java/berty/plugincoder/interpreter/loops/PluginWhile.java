package berty.plugincoder.interpreter.loops;

import java.util.Map;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginWhile {


	private PluginCoder plugin;
	public static boolean active=false;
	public static boolean stopLoop=false;
	public static boolean continueLoop=false;
	public PluginWhile(PluginCoder pluginCoder) {
		plugin=pluginCoder;
	}
	public Object executeWhile(String instruction,String originalInstruction,Map<String,Object>variables) {
		boolean forActivated=PluginFor.active;
		boolean whileActivated=PluginWhile.active;
		active=true;PluginFor.active=false;
		String booleanString=plugin.getReader().getParamsInFunction(instruction,"");
		Object resultado=Void.class;
		while(getBoolean(booleanString,originalInstruction,variables)) {
			resultado=plugin.getCodeExecuter().executeFunction(instruction, originalInstruction, variables);
			if(continueLoop){continueLoop=false;continue;}
			if(stopLoop){stopLoop=false;break;}
			if(!resultado.equals(Void.class))break;
		}
		PluginFor.active=forActivated;
		PluginWhile.active=whileActivated;
		return resultado;
	}
	
	private boolean getBoolean(String booleanString,String originalInstruction,Map<String,Object>variables) {
		Object resultado=plugin.getLogic().executeBoolean(booleanString, originalInstruction, variables);
		if(resultado==null) {
			ErrorManager.nullVariable(booleanString, originalInstruction);
			return false;
		}else if(!resultado.getClass().equals(Boolean.class)) {
			ErrorManager.isNotBoolean(booleanString, originalInstruction);
			return false;
		}
		return (boolean) resultado;
	}
}
