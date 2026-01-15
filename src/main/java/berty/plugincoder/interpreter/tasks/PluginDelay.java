package berty.plugincoder.interpreter.tasks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginDelay {

	private PluginCoder plugin;
	public PluginDelay(PluginCoder plugin) {
		this.plugin=plugin;
	}
	public void executeDelay(String instruction,String originalInstruction,Map<String,Object> variables) {
		String number=plugin.getReader().getParamsInFunction(instruction,"");
		if(number.isEmpty()) {
			ErrorManager.emptyNumberInDelay(instruction);
			return;
		}
		double seconds;
		try {
			Object resultado=plugin.getCodeExecuter().executeInstruction(number, originalInstruction, variables);
			if(resultado==null) {
				ErrorManager.nullVariable(number, originalInstruction);return;
			}
			seconds=Double.parseDouble(resultado.toString());
		}catch (Exception e) {
			ErrorManager.isNotNumber(number, instruction);
			return;
		}
		Map<String, Object> variablesClone=new HashMap<>(variables);
		Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, ()-> {
			plugin.getCodeExecuter().executeFunction(instruction, originalInstruction, variablesClone);
		}, (int)(seconds*20));
	}
}
