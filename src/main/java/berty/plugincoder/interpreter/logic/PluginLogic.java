package berty.plugincoder.interpreter.logic;

import java.util.*;

import java.util.stream.Collectors;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.interpreter.objects.PluginObjectInstance;
import berty.plugincoder.main.PluginCoder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PluginLogic {

	private PluginCoder plugin;
	public PluginLogic(PluginCoder plugin) {
		this.plugin=plugin;
	}
	public Object executeBoolean(String instruction,String originalInstruction,Map<String,Object> variables) {
		instruction=instruction.trim();
		if(instruction.equalsIgnoreCase("true"))return true;
		else if(instruction.equalsIgnoreCase("false"))return false;
		else if(variables.get(instruction)!=null)return variables.get(instruction);
		else {
			String booleanInstruction=instruction.replace("!true", "false");
			booleanInstruction=booleanInstruction.replace("!false", "true");
			return calculateBoolean(booleanInstruction,originalInstruction,variables);
		}
	}
	private Boolean calculateBoolean(String instruction,String originalInstruction,Map<String,Object> variables) {
		Boolean booleanResult = null;
		String[] booleans=plugin.getCodeExecuter().getElementsNotModifyingParentesis(instruction, new String[]{" and ", " or "});
		List<String> operadores= Arrays.stream(plugin.getCodeExecuter().getElementsNotModifyingParentesis(instruction, new String[]{" "}))
				.filter(op->op.equals("and")||op.equals("or")).collect(Collectors.toList());
		int operadorIndex=-1;
		for(String booleanInst:booleans){
			boolean neg=false;
			if(booleanInst.startsWith("!")){
				neg=true;booleanInst=booleanInst.substring(1,booleanInst.length());
			}
			if(booleanResult!=null){
				if(operadores.get(operadorIndex).equals("or")&&booleanResult)return true;
				else if(operadores.get(operadorIndex).equals("and")&&!booleanResult){
					operadorIndex++;continue;
				}
			}
			boolean instResult;
			if(!(booleanInst.startsWith("(")&&booleanInst.endsWith(")")))instResult=calculateBooleanInst(booleanInst,originalInstruction,variables);
			else instResult=calculateBoolean(booleanInst.substring(1,booleanInst.length()-1),originalInstruction,variables);
			instResult=neg?!instResult:instResult;
			if(booleanResult!=null)booleanResult=calculateLogic(operadores.get(operadorIndex),booleanResult,instResult);
			else booleanResult=instResult;
			operadorIndex++;

		}
		return booleanResult;
	}
	private Boolean calculateLogic(String op,boolean b1,boolean b2) {
		if(op.equalsIgnoreCase("and")) {
			return b1&&b2;
		}else if(op.equalsIgnoreCase("or")) {
			return b1||b2;
		}
		return null;
	}
	private boolean calculateBooleanInst(String booleanS,String originalInstruction,Map<String,Object> variables){
		boolean neg=false;
		if(booleanS.equalsIgnoreCase("true"))return true;
		else if(booleanS.equalsIgnoreCase("false"))return false;
		Object resultado;
		String finalBool=booleanS.startsWith("!")?booleanS.substring(1):booleanS;
		if(finalBool.matches("^([^\\s]+) is ([^\\s]+)$")){
			resultado=getBooleanFromTypeEquality(finalBool,originalInstruction,variables);
		}else{
			booleanS=booleanS.trim();
			if(booleanS.isEmpty())return false;
			if(booleanS.split("<=").length==2)resultado=this.getBooleanFromEquality(finalBool,"<=",originalInstruction,variables);
			else if(booleanS.split(">=").length==2)resultado=this.getBooleanFromEquality(finalBool,">=",originalInstruction,variables);
			else if(booleanS.split("<").length==2)resultado=this.getBooleanFromEquality(finalBool,"<",originalInstruction,variables);
			else if(booleanS.split(">").length==2)resultado=this.getBooleanFromEquality(finalBool,">",originalInstruction,variables);
			else if(booleanS.split("=").length==2)resultado=this.getBooleanFromEquality(finalBool,"=",originalInstruction,variables);
			else if(booleanS.split(" is ").length==2)resultado=this.getBooleanFromEquality(finalBool," is ",originalInstruction,variables);
			else resultado=plugin.getCodeExecuter().executeInstruction(finalBool,originalInstruction, variables);
		}
		if(booleanS.startsWith("!"))neg=true;
		if(resultado==null||!resultado.getClass().equals(Boolean.class)) {
			ErrorManager.isNotBoolean(finalBool, originalInstruction);
			return false;
		}
		return neg?!((boolean)resultado):((boolean)resultado);
	}
	private Object getBooleanFromTypeEquality(String instruction,String originalInstruction,Map<String,Object> variables) {
		String[] partes=instruction.split(" is ");
		Object resultado=getVarValue(partes[0], variables, originalInstruction);
		if(partes[1].equalsIgnoreCase("Player"))return (resultado instanceof Player);
		else if(partes[1].equalsIgnoreCase("Entity"))return (resultado instanceof Entity);
		else return (resultado instanceof PluginObjectInstance && ((PluginObjectInstance) resultado).getBaseObject().getName().equals(partes[1]));
	}
	private Object getBooleanFromEquality(String instruction,String operator,String originalInstruction,Map<String,Object> variables) {
		String[] partes=instruction.split(operator);
		String var0=partes[0].endsWith("!")?partes[0].substring(0,partes[0].length()-1):partes[0];
		Object resultado0=getVarValue(var0, variables, originalInstruction);
		Object resultado1=getVarValue(partes[1], variables, originalInstruction);
		if(operator.equals("=")){
			if((resultado0!=null&&resultado0.getClass().isEnum())||(resultado1!=null&&resultado1.getClass().isEnum())){
				boolean equals = (resultado0+"").equals(resultado1+"");
				return partes[0].endsWith("!")?!equals:equals;
			}
			boolean booleanRes=resultado0==resultado1||resultado0.equals(resultado1);
			return partes[0].endsWith("!")?!booleanRes:booleanRes;
		}
		else {
			if(!plugin.getMath().isNumber(resultado0)) {
				ErrorManager.isNotNumber(var0, originalInstruction);
				return Void.class;
			}
			else if(!plugin.getMath().isNumber(resultado1)) {
				ErrorManager.isNotNumber(partes[1], originalInstruction);
				return Void.class;
			}
			double res0=resultado0.getClass().equals(Double.class)?(double)resultado0:(double)((int)resultado0);
			double res1=resultado1.getClass().equals(Double.class)?(double)resultado1:(double)((int)resultado1);
			if(operator.equals("<="))return res0<=res1;
			else if(operator.equals(">="))return res0>=res1;
			else if(operator.equals("<"))return res0<res1;
			else if(operator.equals(">"))return res0>res1;
		}
		return null;
	}
	private Object getVarValue(String var,Map<String,Object> variables,String originalInstruction) {
		if(variables.get(var)!=null) {
			return variables.get(var);
		}
		String type=plugin.getCodeExecuter().getVarInstructionType(var,variables);
		if(type.equalsIgnoreCase("Math")) return plugin.getMath().executeMath(var,originalInstruction,variables);
		else if(type.equalsIgnoreCase("Execution"))return plugin.getCodeExecuter().executeInstruction(var,originalInstruction, variables);
		return null;
	}
	public String[] getBooleans(String operacion,String[] operadores) {
		return plugin.getCodeExecuter().getElements(operacion,operadores);
	}
	public boolean isBoolean(String instruccion, Set<String> variables) {
		if(variables.contains(instruccion))return false;
		String[] booleans=plugin.getCodeExecuter().getElements(instruccion,new String[]{" and "," or ","(",")"});
		for(String booleanInst:booleans){
			String[] separators=getBooleans(booleanInst,new String[]{"<",">","="," is ","!"});
			if(separators.length<=1)return false;
		}
		return true;
	}
}
