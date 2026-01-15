package berty.plugincoder.interpreter.math;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import berty.plugincoder.interpreter.error.ErrorManager;
import berty.plugincoder.main.PluginCoder;

public class PluginMath {

	private PluginCoder plugin;
	private List<Character> numbers=new ArrayList<>();
	private List<Character> operators=new ArrayList<>();
	public PluginMath(PluginCoder pluginCoder) {
		plugin=pluginCoder;
		numbers.add('0');numbers.add('1');numbers.add('2');numbers.add('3');numbers.add('4');
		numbers.add('5');numbers.add('6');numbers.add('7');numbers.add('8');numbers.add('9');
		operators.add('+');operators.add('-');operators.add('*');operators.add('/');operators.add('%');
	}
	public boolean isMath(String instruccion, Set<String> variables) {
		if(variables.contains(instruccion))return false;
		String[] numeros=getNumbers(instruccion);
		if(numeros.length<=1)return false;
		/*for(int i=0;i<numeros.length-1;i++){
			try{Double.parseDouble(numeros[i]);continue;}catch (Exception e){}
			String variable=numeros[i];
			if(plugin.getCodeExecuter().instructionIsVar(variable,variables))continue;
			if(i==0){
				if(!plugin.getCodeExecuter().instructionIsVar(numeros[1],variables))return false;
			}else{
				boolean isVar=false;
				isVar=plugin.getCodeExecuter().instructionIsVar(numeros[i+1],variables);
				if(!isVar&&!plugin.getCodeExecuter().instructionIsVar(numeros[i-1],variables))return false;
			}
			if(plugin.getCodeExecuter().instructionIsVar(numeros[i-1],variables))return false;
		}*/
		for(String num: numeros) {
			String var=num.split("\\.") [0];
			Object res= checkDoubleLong(var);
			if(res!=null)continue;
			if(!variables.contains(var))return false;
		}
		return true;
	}
	public String[] getNumbers(String operacion) {
        return plugin.getCodeExecuter().getElements(operacion,new String[]{"(",")","+", "-", "*", "/","%"});
	}
	public Object checkDoubleLong(String instruction) {
		try {
			double resultado=Double.parseDouble(instruction);
			if(instruction.contains(".")) return resultado;
			else return (int)resultado;
		}catch (Exception e) {return null;}
	}
	public Object executeMath(String instruccion,String originalInstruction,Map<String, Object> variables) {
		List<Double> numbersValues=new ArrayList<>();
		String[] numeros=this.getNumbers(instruccion);
		for(int index=0;index<numeros.length;index++) {
			String number=numeros[index];
			try {
				double d=Double.parseDouble(number);
				numbersValues.add(d);
			}catch (Exception e) {
				if(variables.get(number)!=null&&isNumber(variables.get(number))) {
					double d=Double.parseDouble(variables.get(number).toString());
					numbersValues.add(d);
				}else if(number.split("\\.").length>=2) {
					Object executedNumber=plugin.getCodeExecuter().executeInstruction(number,originalInstruction, variables);
					if(executedNumber!=null&&isNumber(executedNumber)) {
						double d=Double.parseDouble(executedNumber.toString());
						numbersValues.add(d);
					}else {
						ErrorManager.isNotNumber(number, originalInstruction);
						return Void.class;
					}
				}else {
					ErrorManager.isNotNumber(number, originalInstruction);
					return Void.class;
				}
			}
		}
		String numberedInstruction=instruccion;
		for(int index=0;index<numeros.length;index++) {
			String number=numeros[index];
			try {
				Double.parseDouble(number);
			}catch (Exception e) {
				numberedInstruction=numberedInstruction.replace(number, String.valueOf(numbersValues.get(index)));
			}
		}
		numberedInstruction=numberedInstruction.replace("+-", "-");
		numberedInstruction=cambiarOperadoresNegConsecutivos(numberedInstruction,"*");
		numberedInstruction=cambiarOperadoresNegConsecutivos(numberedInstruction,"/");
		double resultado= calculateOperation(numberedInstruction);
		if(String.valueOf(resultado).endsWith(".0")) return (int)resultado;
		else return resultado;
	}
	private double calculateOperation(String operation) {
		int parentesisCount=0;
		boolean parentesisFound=false;
		List<Double> numeros=new ArrayList<>();
		List<String> operadores=new ArrayList<>();
		String numberString="";
		String subOperation="";
		int charCount=0;
		for(Character c:operation.toCharArray()) {
			charCount++;
			if(c.equals('(')||c.equals(')')) {
				if(c.equals('('))parentesisCount++;
				else parentesisCount--;
				parentesisFound=true; 
			}
			if(parentesisFound) {
				subOperation+=String.valueOf(c);
			}else {
				if(operators.contains(c)) {
					operadores.add(String.valueOf(c));
					if(!numberString.isEmpty()) {
						double d=Double.parseDouble(numberString);
						numeros.add(d);
						numberString="";
					}
				}else numberString+=String.valueOf(c);
			}
			if(parentesisCount==0&&parentesisFound) {
				double d=calculateOperation(subOperation.substring(1,subOperation.length()-1));
				numeros.add(d);
				subOperation="";
				parentesisFound=false;
			}
			if(charCount==operation.length()) {
				if(!numberString.isEmpty()) {
					double d=Double.parseDouble(numberString);
					numeros.add(d);
				}
			}
		}
		if(operation.charAt(0)=='-') {
			double neg=numeros.get(0);
			neg=neg*-1;numeros.remove(0);
			numeros.add(0, neg);operadores.remove(0);
		}
		double resultado=0;
		if(numeros.size()==1)resultado=numeros.get(0);
		while(numeros.size()!=1) {
			for(int index=0;index<operadores.size();index++) {
				double num1=numeros.get(index);
				double num2=numeros.get(index+1);
				String operador=operadores.get(index);
				if(operador.equals("+")||operador.equals("-")) {
					boolean stop=false;
					for(int index2=index+1;index2<operadores.size();index2++) {
						String operador2=operadores.get(index2);
						if(operador2.equals("*")||operador2.equals("/")) {
							stop=true;break;
						}
					}
					if(stop)continue;
				}
				double calculo=calculateMath(operador,num1,num2);
				numeros.remove(index+1);numeros.remove(index);operadores.remove(index);
				numeros.add(index, calculo);resultado=calculo;break;
			}
		}
		return resultado;
	}
	private String cambiarOperadoresNegConsecutivos(String numberedInstruction,String operador) {
		String resultado=numberedInstruction;
		while(resultado.contains(operador+"-")) {
			int index=numberedInstruction.indexOf(operador+"-");
			String number="";
			int parentesisCount=0;
			for(int in=index+2;in<numberedInstruction.length();in++) {
				Character c=numberedInstruction.charAt(in);
				if(c.equals('('))parentesisCount++;
				else if(c.equals(')'))parentesisCount--;
				if(operators.contains(c)) {
					if(parentesisCount==0)break;
					else number+=String.valueOf(c);
				}else number+=String.valueOf(c);
			}
			resultado=resultado.replace(operador+"-"+number, operador+"(-"+number+"*1)");
		}
		return resultado;
	}
	private Double calculateMath(String c,Double num1,Double num2) {
		Double resultado;
		if(c.equals("+")) {
			resultado=num1+num2;
		}else if(c.equals("-")) {
			resultado=num1-num2;
		}else if(c.equals("*")) {
			resultado=num1*num2;
		}else if(c.equals("/")){
			resultado=num1/num2;
		}else {
			resultado=num1%num2;
		}
		return resultado;
	}
	public boolean isNumber(Object object) {
		if(object.getClass().equals(Long.class)||object.getClass().equals(Double.class)
		||object.getClass().equals(Integer.class)||object.getClass().equals(Float.class)
		||object.getClass().equals(long.class)||object.getClass().equals(double.class)
		||object.getClass().equals(int.class)||object.getClass().equals(float.class))return true;
		return false;
	}
}
