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
		Double secondsDelay=0d,secondsRepeat;
		String paramsString=plugin.getReader().getParamsInFunction(instruction,"");
		if(paramsString.isEmpty()) {
			ErrorManager.emptyNumberInRepeat(originalInstruction);
			return Void.class;
		}
		String[] params=paramsString.split(",");
		secondsRepeat=getRepeatNumber(params[0],originalInstruction,variables);
		if(secondsRepeat==null)return Void.class;
		if(params.length==2) {
			secondsDelay=secondsRepeat;
			secondsRepeat=getRepeatNumber(params[1],originalInstruction,variables);
			if(secondsRepeat==null)return Void.class;
		}
		List<Integer> tasksClone=new ArrayList<>(taskIDs);
		Map<String,Object> repeatVars=new HashMap<>(variables);
		taskIDs.add(Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin,()-> {
			int taskID=taskIDs.stream().filter(id->!tasksClone.contains(id)).findFirst().get();
			Map<String,Object> repeatVarsClone=new HashMap<>(repeatVars);
			repeatVarsClone.put("task"+taskID,taskID);
			plugin.getCodeExecuter().executeFunction(instruction, originalInstruction,repeatVarsClone);
			if(PluginCoder.isErrorFound()) {
				ErrorManager.errorFoundInRepeat();
				Bukkit.getScheduler().cancelTask(taskID);
				taskIDs.remove((Object)taskID);
			}
		},(long)(secondsDelay*20),(long)(secondsRepeat*20)));

		return taskIDs.stream().filter(taskID->!tasksClone.contains(taskID)).findFirst().get();
	}
	private Double getRepeatNumber(String value,String originalInstruction,Map<String,Object> variables){
		try {
			Object secondsDelayValue=plugin.getCodeExecuter().executeInstruction(value, originalInstruction, variables);
			if(secondsDelayValue==null) {
				ErrorManager.nullVariable(value, originalInstruction);return null;
			}
			return Double.parseDouble(secondsDelayValue.toString());
		}catch (Exception e) {
			ErrorManager.isNotNumber(value, originalInstruction);
			return null;
		}
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
			}else ErrorManager.cancelWithoutRepeat(originalInstruction);
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
