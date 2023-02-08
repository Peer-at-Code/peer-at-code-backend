package be.jeffcheasey88.peeratcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;

public class Configuration {
	
	private String db_host;
	private String db_port;
	private String db_user;
	private String db_database;
	private String db_password;
	
	private File _file;
	
	public Configuration(String path){
		this._file = new File(path);
		System.out.println("Config on "+_file.getAbsolutePath());
	}
	
	public void load() throws Exception{
		if(!this._file.exists()) return;
		BufferedReader reader = new BufferedReader(new FileReader(this._file));
		String line;
		while((line = reader.readLine()) != null){
			String[] split = line.split("=");
			Field field = getClass().getDeclaredField(split[0]);
			if(field == null) continue;
			field.setAccessible(true);
			field.set(this, split[1]);
		}
		reader.close();
	}
	
	public void save() throws Exception{
		if(!_file.exists()){
			File parent = _file.getParentFile();
			if(!parent.exists()) parent.mkdirs();
			_file.createNewFile();
		}
		Field[] fields = getClass().getDeclaredFields();
		BufferedWriter writer = new BufferedWriter(new FileWriter(_file));
		for(Field field : fields){
			field.setAccessible(true);
			if(field.getName().startsWith("_")) continue;
			Object value = field.get(this);
			writer.write(field.getName()+"="+value);
		}
		writer.flush();
		writer.close();
	}
	
	public String getDbHost(){
		return this.db_host;
	}
	
	public String getDbPort(){
		return this.db_port;
	}
	
	public String getDbUser(){
		return this.db_user;
	}
	
	public String getDbDatabase(){
		return this.db_database;
	}
	
	public String getDbPassword(){
		return this.db_password;
	}
}