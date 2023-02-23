package be.jeffcheasey88.peeratcode.parser.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*([^;]*);).*$");
	
	private int modifier;
	private String name;
	private String type;
	
	public Variable(){}
	
	public int parse(String content) throws Exception{
		Matcher matcher = PATTERN.matcher(content);
		matcher.matches();
		
		String[] split = matcher.group(2).split("\\s+");
		for(int i = 0; i < split.length-2; i++){
			this.modifier+=JavaParser.getModifier(split[i]);
		}
		this.name = split[split.length-1];
		this.type = split[split.length-2];
		
		return matcher.group(1).length();
	}
	
	public int getModifier(){
		return this.modifier;
	}
	
	public String getName(){
		return this.name;
	}
	
	public String getType(){
		return this.type;
	}
}
