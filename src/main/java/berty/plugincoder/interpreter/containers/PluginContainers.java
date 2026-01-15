package berty.plugincoder.interpreter.containers;

import java.util.*;

import berty.plugincoder.main.PluginCoder;

public class PluginContainers {

	private PluginCoder plugin;
	public PluginContainers(PluginCoder pluginCoder) {
		plugin=pluginCoder;
	}

	public List<Object> getListFromInstruction(String instruction,String originalInstruction,Map<String,Object>variables){
		List<Object> list=new ArrayList<>();
		String objects="("+instruction.substring(1,instruction.length()-1)+")";
		if(!objects.isEmpty()) {
			for(String object:plugin.getCodeExecuter().getStringParameters(objects)) {
				Object resultado=plugin.getCodeExecuter().executeInstruction(object, originalInstruction, variables);
				if(PluginCoder.isErrorFound()) return null;
				list.add(resultado);
			}
		}
		return list;
	}

	public Object getMapFromInstruction(String instruction, String originalInstruction, Map<String, Object> variables) {
		Map<Object,Object> map=new HashMap<>();
		String objects="("+instruction.substring(1,instruction.length()-1)+")";
		if(!objects.isEmpty()) {
			for(String keyValue:plugin.getCodeExecuter().getStringParameters(objects)) {
				String[] keyValueContainer=plugin.getCodeUtils().getElementsBySeparator(keyValue,':');
				Object key=plugin.getCodeExecuter().executeInstruction(keyValueContainer[0], originalInstruction, variables);
				Object value=plugin.getCodeExecuter().executeInstruction(keyValueContainer[1], originalInstruction, variables);
				if(PluginCoder.isErrorFound()) return null;
				map.put(key,value);
			}
		}
		return map;
	}
}
