package berty.plugincoder.interpreter.error;

import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import berty.plugincoder.main.PluginCoder;
import org.bukkit.entity.Player;

public class ErrorManager {

	private static CommandSender sender=Bukkit.getConsoleSender();
	public static void setSender(CommandSender sender) {
		ErrorManager.sender = sender;
	}

	public static Map<String,String> errorTranslation=new HashMap<>();

	public static Map<String, String> getErrorTranslation() {
		return errorTranslation;
	}

	private static void triggerVarError(String instruction,String messageId){
		PluginCoder.setErrorFound(true);
		String errorMessage=errorTranslation.get(messageId);
		sender.sendMessage(ChatColor.RED+errorMessage);
		if(!instruction.isEmpty()){
			sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+" :"+ChatColor.YELLOW+instruction);
		}
	}
	public static boolean checkTextVariable(String variable, String instruction, PluginCoder plugin) {
		try {
			Double.parseDouble(variable);
			triggerVarError(instruction,"errorVariableNotNumber");
			return false;
		}catch (Exception e) {}
		try {
			Double.parseDouble(String.valueOf(variable.charAt(0)));
			triggerVarError(instruction,"errorVariableStartsWithNumber");
			return false;
		}catch (Exception e) {}
		if(plugin.getReservedWords().contains(variable.toLowerCase())) {
			triggerVarError(instruction,"errorReservedWords");
			return false;
		}
		if(variable.contains(" ")) {
			boolean spaceFound=false;
			for(Character c:variable.toCharArray()) {
				if(c.equals(' '))spaceFound=true;
				if(!c.equals(' ')&&spaceFound) {
					PluginCoder.setErrorFound(true);
					sender.sendMessage(ChatColor.RED+errorTranslation.get("errorVariableWithSpaces"));
					if(!instruction.isEmpty()){
						sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
					}
					return false;
				}
			}
		}
		List<String> notAcceptSymbols = new ArrayList<>(Arrays.asList(";", ",", "&", "%", "|", "!", "@", "¡", "€", "¿", ".", "-",
				"+", "*", "/", "(", ")", "ª", "º", "{", "}", "[", "]", "`", "´", "^"));
		Optional<String> caracterNotAcceptable=notAcceptSymbols.stream().filter(s->variable.contains(s)).findFirst();
		if(caracterNotAcceptable.isPresent()){
			PluginCoder.setErrorFound(true);
			sender.sendMessage(ChatColor.RED+errorTranslation.get("errorVariableContainsSymbol")+": "+ChatColor.YELLOW+caracterNotAcceptable.get());
			if(!instruction.isEmpty()){
				sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
			}
			return false;
		}
		return true;
	}
	private static void varError(String messageId,String variable, String instruction){
		PluginCoder.setErrorFound(true);
		String errorMessage=errorTranslation.get(messageId);
		errorMessage=errorMessage.replace("%var%",ChatColor.YELLOW+variable+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
		if(!instruction.isEmpty())sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void varNotExists(String variable,String instruction) {
		varError("varNotExists",variable,instruction);
	}
	public static void nullVariable(String variable,String instruction) {
		varError("varIsNull",variable,instruction);
	}
	public static void isNotNumber(String variable,String instruction) {
		varError("varIsNotNumber",variable,instruction);
	}
	public static boolean checkExecutedVariable(Object variable,String instruction,String executedCode,String executedMethod) {
		if(!variable.equals((Object)Void.class)&&variable!=null)return true;
		methodError("executionIsNull",instruction,executedCode,executedMethod);
		return false;
	}
	private static void methodError(String messageId,String instruction, String executedCode,String methodName){
		PluginCoder.setErrorFound(true);
		String errorMessage=errorTranslation.get(messageId);
		errorMessage=errorMessage.replace("%executedCode%",ChatColor.YELLOW+executedCode+ChatColor.RED+"");
		errorMessage=errorMessage.replace("%method%",ChatColor.YELLOW+methodName+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void methodNotFound(String methodName, String instruction,String executedCode) {
		methodError("methodDoesNotExistFor",instruction,executedCode,methodName);
	}
	public static void methodExecutionFailed(String methodName, String instruction,String executedCode) {
		methodError("methodExecutionFailed",instruction,executedCode,methodName);
	}
	public static void isNotBoolean(String variable,String instruction) {
		varError("varIsNotBoolean",variable,instruction);
	}
	public static void isNotString(String variable,String instruction) {
		varError("varIsNotText",variable,instruction);
	}
	public static void elseWithoutIf(String instruction) {
		PluginCoder.setErrorFound(true);
		String conditional=instruction.startsWith("else if")?"Else If":"Else";
		String errorMessage=errorTranslation.get("errorPlaceConditional");
		errorMessage=errorMessage.replace(" conditional ",ChatColor.YELLOW+" "+conditional+" "+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
		if(instruction.equals("else if"))instruction="";
		if(!instruction.isEmpty()){
			sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
		}
	}
	public static void emptyBooleanArgumentInConditional(String instruction) {
		PluginCoder.setErrorFound(true);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("errorEmptyBooleanInIf"));
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void emptyNumberInDelay(String instruction) {
		PluginCoder.setErrorFound(true);

		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void emptyNumberInRepeat(String instruction) {
		PluginCoder.setErrorFound(true);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("errorEmptyNumberInRepeat"));
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void emptyNumberInCancelRepeat(String instruction) {
		PluginCoder.setErrorFound(true);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("errorEmptyNumberInRepeatCancel"));
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void repeatInsideOfRepeat(String instruction) {
		PluginCoder.setErrorFound(true);
		String errorMessage = errorTranslation.get("errorRepeatInRepeat");
		sender.sendMessage(ChatColor.RED+errorMessage);
		if(!instruction.isEmpty()){
			sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
		}
	}
	public static void cancelWithoutRepeat(String instruction) {
		PluginCoder.setErrorFound(true);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("errorCancelWithoutParam"));
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void errorFoundInRepeat() {
		sender.sendMessage(ChatColor.RED+errorTranslation.get("cancelRepeat"));
	}
	public static void notIterableParam(String var, String instruction) {
		varError("isNotIterable",var,instruction);
	}
	public static void forWithOneParamOnly(String param, String instruction) {
		PluginCoder.setErrorFound(true);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("errorForParams"));
		String forExample=errorTranslation.get("forExample");
		forExample=forExample.replace("var:list",ChatColor.YELLOW+param+":list"+ChatColor.RED+"");
		forExample=forExample.replace("var:0->10",ChatColor.YELLOW+param+":0->10"+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+forExample);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void existingVariableAsForVariable(String var, String instruction) {
		varError("existingVarAsForVariable",var,instruction);
	}
	public static void badSyntaxInForRange(String rangeText, String instruction) {
		PluginCoder.setErrorFound(true);
		String errorForSyntax=errorTranslation.get("errorForSyntax");
		errorForSyntax=errorForSyntax.replace("%range%",ChatColor.YELLOW+rangeText+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorForSyntax);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("rangeDeclared"));
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void errorInListener(String eventName) {
		String errorMessage=errorTranslation.get("errorEvent");
		errorMessage=errorMessage.replace("%event%",ChatColor.YELLOW+eventName+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
	}
	public static void wrongNumParamsInFunction(String functionName, int paramsNumber,String instruction) {
		PluginCoder.setErrorFound(true);
		String errorMessage=errorTranslation.get("errorFunctionParams");
		errorMessage=errorMessage.replace("%function%",ChatColor.YELLOW+functionName+ChatColor.RED+"");
		errorMessage=errorMessage.replace("%params%",ChatColor.YELLOW+""+paramsNumber+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
		sender.sendMessage(ChatColor.RED+errorTranslation.get("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}
	public static void settingVoidValueInObjectProperty(String property,String instruction) {
		PluginCoder.setErrorFound(true);
		String errorMessage=PluginCoder.getCoderGUI().getGuiText("errorEmptyValue");
		errorMessage=errorMessage.replace("%property%",ChatColor.YELLOW+" "+property+" "+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
		sender.sendMessage(ChatColor.RED+PluginCoder.getCoderGUI().getGuiText("codeLineText")+": "+ChatColor.YELLOW+instruction);
	}

	//gui errors
	public static void existingPluginWithThisName(String name) {
		String errorMessage=errorTranslation.get("errorPluginName");
		errorMessage=errorMessage.replace("%plugin%",ChatColor.YELLOW+" "+name+" "+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
	}

    public static void notNumberInstruction() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("notNumberInstruction"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
    }

	public static void consecutiveMathSymbols() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("consecutiveMathSymbols"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}

	public static void notExecutableSequence() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("notExecutableSequence"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}

	public static void somePlayerHasMenuOpened(Player p) {
		p.sendMessage(ChatColor.RED+errorTranslation.get("menuSameTime"));
	}

	public static void executeSequenceIsNotANumber() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("notNumberSequence"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}

	public static void differentNumberOfParentesisOpenClose() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("differentParentesisOpenClose"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}
	public static void notConditionalInstruction() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("notConditionalInstruction"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}
	public static void consecutiveLogicSymbols() {
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("consecutiveLogicSymbols"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}
	public static void notConditionalSequence(){
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorTranslation.get("notConditionalSequence"));
		PluginCoder.getCoderGUI().errorSound(PluginCoder.getCoderGUI().getGuiPlayer());
	}

	public static void errorPropertyName(String propertyName) {
		String errorMessage=errorTranslation.get("errorPropertyName");
		errorMessage=errorMessage.replace("%property%",ChatColor.YELLOW+" "+propertyName+" "+ChatColor.RED+"");
		PluginCoder.getCoderGUI().getGuiPlayer().sendMessage(ChatColor.RED+errorMessage);
	}
	public static void errorPlayerNotFound(String playerName) {
		String errorMessage=errorTranslation.get("errorPlayerNotFound");
		errorMessage=errorMessage.replace("%player%",ChatColor.YELLOW+" "+playerName+" "+ChatColor.RED+"");
		sender.sendMessage(ChatColor.RED+errorMessage);
	}
}
