package be.jeffcheasey88.peeratcode.parser.java;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Class {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*([^\\{]*)\\{(.*)\\})\\s*$");
	
	private int modifier;
	private String name;
	
	private List<Variable>vars;
	private List<Function>functions;
	
	public Class(){}

	public int parse(String content) throws Exception{
		Matcher matcher = PATTERN.matcher(content);
		matcher.matches();
		
		String[] split = matcher.group(2).split("\\s+");
		for(int i = 0; i < split.length-1; i++){
			this.modifier+=JavaParser.getModifier(split[i]);
		}
		this.name = split[split.length-1];
		
		this.vars = new ArrayList<>();
		this.functions = new ArrayList<>();
		
		content = matcher.group(3);
		Pattern empty = Pattern.compile("^\\s*$");
		while(!(empty.matcher(content).matches())){
			int quotes = content.indexOf(';');
			int braces = content.indexOf('{');
			int equals = content.indexOf('=');
			if(quotes < braces && quotes < equals){
				Variable variable = new Variable();
				int index = variable.parse(content);
				this.vars.add(variable);
				content = content.substring(index);
			}else if(equals >= 0 && equals < braces){
				//variable with value
				System.out.println("equals < braces");
				break;
			}else{
				Function func = new Function();
				int index = func.parse(content);
				this.functions.add(func);
				content = content.substring(index);
			}
		}
		
		return matcher.group(1).length();
	}
	
	public int getModifier(){
		return this.modifier;
	}
	
	public String getName(){
		return this.name;
	}
	
}
