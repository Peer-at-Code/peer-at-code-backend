package be.jeffcheasey88.peeratcode.parser.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Operation {
	
	private static Pattern VARIABLE_PATTERN = Pattern.compile("^(\\s*([^;]*)).*$");
	
	private String tmp;
	
	public Operation(){
		
	}
	
	public int parse(String content) throws Exception{
		Matcher matcher = VARIABLE_PATTERN.matcher(content);
		if(matcher.matches()){
			this.tmp = matcher.group(2);
			System.out.println("parsed "+tmp);
			return matcher.group(1).length()+1;
		}
		return 0;
	}

}
