package be.jeffcheasey88.peeratcode.parser.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Import {
	
	private static Pattern PATTERN = Pattern.compile("^\\s*(import\\s+([^;]*);).*$");
	
	public static boolean isImport(String content){
		return PATTERN.matcher(content).matches();
	}
	
	private String name;
	
	public Import(){}
	
	public int parse(String content) throws Exception{
		Matcher matcher = PATTERN.matcher(content);
		matcher.matches();
		this.name = matcher.group(2);
		return matcher.group(1).length();
	}

	public String getName(){
		return this.name;
	}
}
