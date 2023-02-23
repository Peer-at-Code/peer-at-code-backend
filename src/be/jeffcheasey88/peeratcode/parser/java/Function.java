package be.jeffcheasey88.peeratcode.parser.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*([^(]*)\\(([^)]*)\\)\\s*([^{]*)\\{)(.*)$");
	
	private int modifier;
	private String name;
	private String exceptions;
	private String parameters;
	
	public Function(){
		
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
			System.out.println();
			int end = body.indexOf('}');
			int braces = body.indexOf('{');
			int quotes = body.indexOf(';');
			
			if((end < 0) || (end < braces && end < quotes)){
//				System.out.println("no INDEX in "+body);
//				if(end > 0) offset+=end;
				break;
			}
			
//			System.out.println(toString()+" - "+offset+" | "+end);
			if(braces < 0 && quotes < 0){
				System.out.println("OUT "+body);
				if(end > 0) offset+=end;
				break;
			}
			
			if(braces >= 0 && braces < quotes){
				Function func = new Function();
				index = func.parse(body.substring(0, end+1));
			}else{
				Operation op = new Operation();
				index = op.parse(body.substring(0, end));
			}
			offset+=index+1;
//			System.out.println("SEEKINDEX "+index+" "+toString());
//			System.out.println("FROM("+body.length()+") "+body);
//			System.out.println();
			body = body.substring(index);
		}while(offset > -1);
//		System.out.println(toString()+": "+(matcher.group(1).length()+offset+1));
//		System.out.println("\t\t\t\t("+content.length()+")\t"+content);
		return matcher.group(1).length()+offset;
	}

	@Override
	public String toString(){
		return "Function[name="+name+",param="+parameters+",exception="+exceptions+"]";
	}
}
