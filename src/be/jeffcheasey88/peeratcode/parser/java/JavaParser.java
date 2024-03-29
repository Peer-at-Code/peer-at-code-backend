package be.jeffcheasey88.peeratcode.parser.java;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class JavaParser {
	
	public static void main(String[] args) throws Exception {
		File file = new File("C:\\Users\\jeffc\\eclipse-workspace\\peer-at-code-backend\\src\\be\\jeffcheasey88\\peeratcode\\parser\\java\\Import.java");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		JavaParser parser = new JavaParser(reader);
		parser.parse();
		System.out.println("SHOW-----------------");
		parser.show();
	}
	
	private Package pack;
	private List<Import> imports;
	private Class clazz;
	
	private BufferedReader reader;
	
	public JavaParser(BufferedReader reader){
		this.reader = reader;
	}

	public void parse() throws Exception{
		String content = "";
		int index;
		
		String line;
		while((line = reader.readLine()) != null) content+=line;
		
		content = CleanerPool.getterToDelete.clean(content);
		
		this.pack = new Package();
		index = this.pack.parse(content);
		content = content.substring(index);
		
		this.imports = new ArrayList<>();
		while(Import.isImport(content)){
			Import imp = new Import();
			index = imp.parse(content);
			this.imports.add(imp);
			content = content.substring(index);
		}
		
		this.clazz = new Class();
		index = this.clazz.parse(content);
		content = content.substring(index);
	}
	
	public Package getPackage(){
		return this.pack;
	}
	
	public List<Import> getImports(){
		return this.imports;
	}
	
	public Class getClazz(){
		return this.clazz;
	}
	
	public void show(){
		System.out.println("package "+this.pack.getName()+";");
		System.out.println();
		for(Import i : this.imports) System.out.println("import "+i.getName()+";");
		System.out.println();
		this.clazz.show();
	}
	
	public static int getModifier(String modifier){
		switch(modifier){
			case "public": return Modifier.PUBLIC;
			case "private": return Modifier.PRIVATE;
			case "protected": return Modifier.PROTECTED;
			case "static": return Modifier.STATIC;
			case "final": return Modifier.FINAL;
			case "synchronized": return Modifier.SYNCHRONIZED;
			case "volatile": return Modifier.VOLATILE;
			case "transient": return Modifier.TRANSIENT;
			case "native": return Modifier.NATIVE;
			case "abstract": return Modifier.ABSTRACT;
			case "strictfp": return Modifier.STRICT;
			default: break;
		}
		return 0;
	}

}
