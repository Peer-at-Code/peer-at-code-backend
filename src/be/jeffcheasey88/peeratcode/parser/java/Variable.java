package be.jeffcheasey88.peeratcode.parser.java;

import java.lang.reflect.Modifier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Variable {
	
	private static Pattern PATTERN = Pattern.compile("^(\\s*)(.*)$");
	
	private int modifier;
	private String name;
	private String type;
	private Variable value;
	
	public Variable(){}
	
	public Variable(int modifier, String type){
		this.modifier = modifier;
		this.type = type;
	}
	
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
		
		int offset = matcher.group(1).length();

		boolean hasEquals = false;
		boolean fromMinus = false;
		String body = matcher.group(2);
		while(true){
			int space = indexOf(body, "\\s+");
			int equals = indexOf(body, "=");
			int quote = indexOf(body,",");
			int quotes = indexOf(body, ";");
			int minus = indexOf(body, "<");
			
			
			int min = min(space, equals, quote, quotes, minus);
			String value = body.substring(0,min);
			System.out.println("'"+value+"'");
			if(hasEquals){
				if(value.isEmpty()){
					do {
						body = body.substring(1);
						offset++;
					}while(indexOf(body, "\\s+") == 0);
					continue;
				}
				this.value = new Value(value);
				body = body.substring(value.length()+1);
				offset+=value.length()+1;
				break;
			}else if(fromMinus){
				System.out.println("fromMinus "+value);
				this.name = value;
				body = body.substring(value.length()+1);
				offset+=value.length()+1;
				break;
			} else if(min == space){
				if(value.isEmpty()){
					do {
						body = body.substring(1);
						offset++;
					}while(indexOf(body, "\\s+") == 0);
					continue;
				}
				int mod = JavaParser.getModifier(value);
				if(mod > 0){
					this.modifier+=mod;
				}else{
					if(type == null){
						this.type = value;
					}else if(name == null){
						this.name = value;
					}
				}
				body = body.substring(value.length()+1);
				offset+=value.length()+1;
			}else if(min == equals){
					if(this.name == null) this.name = value;
					hasEquals = true;
					body = body.substring(value.length()+1);
					offset+=value.length()+1;
			}else if(min == minus){
				value = value+"<";
				System.out.println("MINUS");
				int maxus = 1;
				while(maxus > 0){
					char current = body.charAt(value.length());
					value+=current;
					if(current == '<'){
						maxus++;
					}
					if(current == '>'){
						maxus--;
					}
				}
				this.type = value;
				body = body.substring(value.length());
				offset+=value.length();
				while(indexOf(body, "\\s+") == 0){
					body = body.substring(1);
					offset++;
				}
				fromMinus = true;
				System.out.println("fromMinus on "+body);
			}else if(min == quote){
				if(this.name != null) break;
				this.name = value;
				body = body.substring(value.length());
				offset+=value.length();
				break;
			}else {
				offset+=value.length()+1;
				break;
			}
		}
		
		System.out.println("-------------");
		show(0);
		System.out.println("-------------");
		
		return offset;
	}
	
	private int indexOf(String value, String target){
		return value.split(target)[0].length();
	}
	
	private int min(int... mins){
		int result = mins[0];
		for(int min : mins){
			if(min < result) result = min;
		}
		return result;
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
	
	public Variable getValue(){
		return this.value;
	}
	
	public void show(int tab){
		String start = "";
		for(int i = 0; i < tab; i++) start+="\t";
		System.out.println("type="+type+" | name="+name);
		System.out.println(start+Modifier.toString(modifier)+" "+type+" "+name+";");
	}
	
	public static class Value extends Variable{
		
		private String value;
		
		public Value(String value){
			this.value = value;
		}
		
		public String value(){
			return this.value;
		}
	}
}
