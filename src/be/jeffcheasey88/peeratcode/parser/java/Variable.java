package be.jeffcheasey88.peeratcode.parser.java;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*)(.*)$");
	
	private int modifier;
	private String name;
	private String type;
	
	public Variable(){}
	
	//int i = 4;
	//int i,j,k,l=1;
	//int lm      ;
	//public static int l;
	//Test<Test>t;
	//Test<Test,K,L>         j = new Test().schedule(p -> { return true;});
	//int i =j=k=l=4;
	
	public int parse(String content) throws Exception{
		System.out.println("Variable.parse");
		System.out.println(content);
		Matcher matcher = PATTERN.matcher(content);
		matcher.matches();
		
		return 1;
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
	
	public void show(int tab){
		String start = "";
		for(int i = 0; i < tab; i++) start+="\t";
		System.out.println(start+Modifier.toString(modifier)+" "+type+" "+name+";");
	}
}
