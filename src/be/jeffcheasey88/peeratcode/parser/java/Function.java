package be.jeffcheasey88.peeratcode.parser.java;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*([^(]*)\\(([^)]*)\\)\\s*([^{]*)\\{)(.*)$");
	
	private int modifier;
	private String name;
	private String exceptions;
	private String parameters;
	
	private List<Function> functions;
	private List<Operation> operations;
	
	public Function(){
		this.functions = new ArrayList<>();
		this.operations = new ArrayList<>();
	}
	
	public int parse(String content) throws Exception{
		Matcher matcher = PATTERN.matcher(content);
		matcher.matches();
		
		String[] split = matcher.group(2).split("\\s+");
		for(int i = 0; i < split.length-2; i++){
			this.modifier+=JavaParser.getModifier(split[i]);
		}
		this.name = split[split.length-1];
		this.parameters = matcher.group(3);
		this.exceptions = matcher.group(4);
		
		String body = matcher.group(5);
		int offset = 0;
		int index = 0;
		do {
			int end = body.indexOf('}');
			int braces = body.indexOf('{');
			int quotes = body.indexOf(';');
			
			if((end < 0) || (end < braces && end < quotes)){
				if(end > 0) offset+=end;
				break;
			}
			
			if(braces < 0 && quotes < 0){
				if(end > 0) offset+=end;
				break;
			}
			
			if(braces >= 0 && braces < quotes){
				Function func = new Function();
				index = func.parse(body.substring(0, end+1));
				this.functions.add(func);
			}else{
				Operation op = new Operation();
				index = op.parse(body.substring(0, end+1));
				this.operations.add(op);
			}
			offset+=index+1;
			body = body.substring(index);
		}while(offset > -1);
		return matcher.group(1).length()+offset;
	}
	
	public void show(int tab){
		String start = "";
		for(int i = 0; i < tab; i++) start+="\t";
		System.out.println(start+Modifier.toString(modifier)+" "+name+"("+parameters+") "+exceptions+" {");
		for(Operation o : this.operations) o.show(tab+1);
		System.out.println();
		for(Function f : this.functions) f.show(tab+1);
		System.out.println(start+"}");
	}

	@Override
	public String toString(){
		return "Function[name="+name+",param="+parameters+",exception="+exceptions+"]";
	}
}
