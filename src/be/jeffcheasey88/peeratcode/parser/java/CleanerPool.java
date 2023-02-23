package be.jeffcheasey88.peeratcode.parser.java;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CleanerPool {
	
	public static CleanerPool getterToDelete = new CleanerPool();
	
	private static String CONSTANT_REPLACER = "$STRING_STATEMENT_CONSTANT_";
	
	private List<String> constants;
	
	private CleanerPool(){
		this.constants = new ArrayList<>();
		getterToDelete = this;
	}

	public String clean(String statement){
		char[] chars = statement.toCharArray();
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < chars.length; i++){
			char current = chars[i];
			if(current== '"'){
				int constantPos = this.constants.size();
				String constant = cutConstant(chars, i);
				i+=constant.length()+1;
				builder.append(CONSTANT_REPLACER+constantPos);
				this.constants.add(constant);
			}else{
				builder.append(current);
			}
		}
		
		for(String s : constants){
			System.out.println("CONSTANT="+s);
		}
		return builder.toString();
	}
	
	public boolean isConstant(String region){
		return region.startsWith(CONSTANT_REPLACER);
	}
	
	public String getConstant(String replacer){
		if(!replacer.startsWith(CONSTANT_REPLACER)) return null;
		return this.constants.get(Integer.parseInt(replacer.replace(CONSTANT_REPLACER,"")));
	}
	
	public List<String> getConstants(){
		return this.constants;
	}
	
	private static Pattern parenthesisPattern = Pattern.compile("^\\$SQL_STATEMENT_PARENTHESIS_([0-9]*$)");
	
	public boolean isParenthesis(String region){
		return parenthesisPattern.matcher(region).matches();
	}
	
	private String cutConstant(char[] chars, int pos){
		StringBuilder builder = new StringBuilder();
		for(int i = pos+1; i < chars.length; i++){
			char current = chars[i];
			if(current == '"'){
				if(current == '\\'){ //toChange
					builder.append(current);
				}else break;
			}else{
				builder.append(current);
			}
		}	
		return builder.toString();
	}
	
}