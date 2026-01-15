package berty.plugincoder.interpreter.tasks;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginRepeat {

	private PluginCoder plugin;
	private List<Integer> taskIDs=new ArrayList<>();
	public PluginRepeat(PluginCoder plugin) {
		this.plugin=plugin;
	}
	public Object executeRepeat(String instruction,String originalInstruction,Map<String,Object> variables) {
		double secondsDelay=0;
		double secondsRepeat=0;
		String paramsString=plugin.getReader().getParamsInFunction(instruction,"");
		if(paramsString.isEmpty()) {
			ErrorManager.emptyNumberInRepeat(originalInstruction);
			return Void.class;
		}
		String[] params=paramsString.split(",");
		Object resultado=plugin.getCodeExecuter().executeInstruction(params[0], originalInstruction, variables);
		if(resultado==null) {
			ErrorManager.nullVariable(params[0], originalInstruction);return Void.class;
		}
		try {
			secondsRepeat=Double.parseDouble(resultado.toString());
		}catch (Exception e) {
			ErrorManager.isNotNumber(params[0], originalInstruction);
			return Void.class;
		}
		if(params.length==2) {
			secondsDelay=Double.parseDouble(resultado.toString());
			try {
				Object resultado2=plugin.getCodeExecuter().executeInstruction(params[1], originalInstruction, variables);
				if(resultado2==null) {
					ErrorManager.nullVariable(params[1], originalInstruction);return Void.class;
				}
				secondsRepeat=Double.parseDouble(resultado2.toString());
			}catch (Exception e) {
				ErrorManager.isNotNumber(params[1], originalInstruction);
				return Void.class;
			}
		}
		List<Integer> tasksClone=new ArrayList<>(taskIDs);
		taskIDs.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,()-> {
			int taskID=taskIDs.stream().filter(t->!tasksClone.contains(t)).findFirst().get();
			Map<String,Object> repeatVars=new HashMap<>(variables);
			variables.put("task"+taskID,taskID);
			plugin.getCodeExecuter().executeFunction(instruction, originalInstruction, variables);
			new HashMap<>(variables).keySet().stream().filter(k->!repeatVars.containsKey(k)).forEach(k->variables.remove(k));
			if(PluginCoder.isErrorFound()) {
				ErrorManager.errorFoundInRepeat();
				Bukkit.getScheduler().cancelTask(taskID);
				taskIDs.remove((Object)taskID);
			}
		},(long)secondsDelay*20,(long)secondsRepeat*20));

		return taskIDs.stream().filter(t->!tasksClone.contains(t)).findFirst().get();
	}
	public void executeCancelTask(String instruction,String originalInstruction, Map<String, Object> variables) {
		//ver si el cancel tiene parametro
		if(instruction.equals("cancel()")) {
			ErrorManager.emptyNumberInCancelRepeat(originalInstruction);
			return;
		}
		String instConComa=instruction+";";
		String cancelParam=instConComa.replaceAll("cancel\\((.+)\\);", "$1");
		cancelParam=cancelParam.replaceAll("cancel;", "");
		if(cancelParam.isEmpty()) {
			List<String> tasks=variables.keySet().stream().filter(v->{
				if(!v.startsWith("task"))return false;
				String taskIDString=v.replace("task", "");
				try {
					int taskID=Integer.parseInt(taskIDString);
					if(taskIDs.contains(taskID))return true;
					return false;
				}catch (Exception e) {
					return false;
				}
			}).collect(Collectors.toList());
			if(!tasks.isEmpty()) {
				int taskID=Integer.parseInt(tasks.get(0).replace("task", ""));
				Bukkit.getScheduler().cancelTask(taskID);
				taskIDs.remove((Object)taskID);
			}else {
				ErrorManager.cancelWithoutRepeat(originalInstruction);
			}
		}else {
			try {
				Object resultado=plugin.getCodeExecuter().executeFunction(instruction, originalInstruction, variables);
				int id=(int) Double.parseDouble(resultado.toString());
				Bukkit.getScheduler().cancelTask(id);
				taskIDs.remove((Object)id);
			}catch (Exception e) {
				ErrorManager.isNotNumber(cancelParam, instruction);
			}
		}
		if(taskIDs.isEmpty())PluginCoder.getCoderGUI().getFunctionGUI().stopCodeExecution();
		
	}
	public List<Integer> getTaskIDs() {
		return taskIDs;
	}
}
