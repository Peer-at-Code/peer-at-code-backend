package be.jeffcheasey88.peeratcode.parser.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {
	
	private static Pattern VARIABLE_PATTERN = Pattern.compile("^(\\s*([^;]*)).*$");
	
	private String tmp;
	
	public Operation(){}
	
	public int parse(String content) throws Exception{
		Matcher matcher = VARIABLE_PATTERN.matcher(content);
		if(matcher.matches()){
			this.tmp = matcher.group(2);
			return matcher.group(1).length()+1;
		}
		return 0;
	}
	
	public void show(int tab){
		String start = "";
		for(int i = 0; i < tab; i++) start+="\t";
		System.out.println(start+tmp+";");
	}

}
