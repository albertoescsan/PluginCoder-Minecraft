package berty.plugincoder.interpreter.conditionals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginConditionals {
	private PluginCoder plugin;
	public PluginConditionals(PluginCoder plugin) {
		this.plugin=plugin;
	}
	public Object executeConditional(String instruction,Map<String,Object> variables) {
		Object resultado=Void.class;
		List<String> conditionals=new ArrayList<>();
		int llavesCount=0;
		String condition="";
		for(Character c: instruction.toCharArray()) {
			condition+=String.valueOf(c);
			if(c.equals('{')) {
				llavesCount++;
			}else if(c.equals('}')) {
				llavesCount--;
				if(llavesCount==0) {
					conditionals.add(condition);
					condition="";
				}
			}
		}
		boolean ifChecked=false;
		for(String conditional: conditionals) {
			if(!ifChecked) {
				ifChecked=true;
				if(!conditional.startsWith("if")) {
					ErrorManager.elseWithoutIf(conditional);return Void.class;
				}
			}
			Object[] resultadoExecuted=checkConditionals(conditional,variables);
			if((boolean)resultadoExecuted[1]) {
				resultado=resultadoExecuted[0];break;
			}
			if(PluginCoder.isErrorFound())return Void.class;
		}
		return resultado;
	}
	private Object[] checkConditionals(String conditional,Map<String,Object> variables) {
		Object[] resultadoExecuted=new Object[2];
		resultadoExecuted[0]=Void.class;
		resultadoExecuted[1]=false;
		if(!(conditional.startsWith("else")&&!conditional.startsWith("else if"))) {
			String conditionString="";
			int parentesisCount=0;
			String conditionType=conditional.startsWith("if")?"if":"else if";
			for(Character c: conditional.substring(conditionType.length()).toCharArray()) { 
				if(c.equals('(')) {
					parentesisCount++;
					if(parentesisCount==1)continue;
				}
				else if(c.equals(')')) {
					parentesisCount--;
					if(parentesisCount==0)break;
				}
				conditionString+=String.valueOf(c);
			}
			if(conditionString.isEmpty()) {
				ErrorManager.emptyBooleanArgumentInConditional(conditional);
				resultadoExecuted[1]=false;
				return resultadoExecuted;
			}
			Object conditionResult=plugin.getLogic().executeBoolean(conditionString, conditional, variables);
			if(conditionResult==null) {
				ErrorManager.nullVariable(conditionString, conditional);
				resultadoExecuted[1]=false;
				return resultadoExecuted;
			}
			boolean condition=(boolean) conditionResult;
			if(condition) {
				resultadoExecuted[0]=plugin.getCodeExecuter().executeFunction(conditional,conditional, variables);
				resultadoExecuted[1]=true;
			}
		}else {
			resultadoExecuted[0]=plugin.getCodeExecuter().executeFunction(conditional,conditional, variables);
			resultadoExecuted[1]=true;
		}
		return resultadoExecuted;
	}
}
