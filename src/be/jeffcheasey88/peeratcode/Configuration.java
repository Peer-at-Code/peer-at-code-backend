package be.jeffcheasey88.peeratcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;

public class Configuration {
	
	private String db_host;
	private int db_port;
	private String db_user;
	private String db_database;
	private String db_password;
	
	private int tcp_port;
	private boolean use_ssl;
	private String ssl_keystore;
	private String ssl_keystorePasswd;
	
	
	
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
			injectValue(field, split[1]);
		}
		reader.close();
	}
	
	private void injectValue(Field field, String value) throws IllegalAccessException{
		if(field.getType().isPrimitive()){
			switch(field.getType().getName()){
				case "boolean":
					field.setBoolean(this, Boolean.parseBoolean(value));
					break;
				case "byte":
					field.setByte(this, Byte.parseByte(value));
					break;
				case "char":
					field.setChar(this, value.charAt(0));
					break;
				case "double":
					field.setDouble(this, Double.parseDouble(value));
					break;
				case "float":
					field.setFloat(this, Float.parseFloat(value));
					break;
				case "int":
					field.setInt(this, Integer.parseInt(value));
					break;
				case "long":
					field.setLong(this, Long.parseLong(value));
					break;
				case "short":
					field.setShort(this, Short.parseShort(value));
					break;
				default: throw new IllegalArgumentException(value);
			}
			return;
		}
		if(field.getType().equals(String.class)){
			field.set(this, value);
			return;
		}
		throw new IllegalArgumentException(value);
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
	
	public int getDbPort(){
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
	
	public String getSslKeystore(){
		return this.ssl_keystore;
	}
	
	public String getSslKeystorePasswd(){
		return this.ssl_keystorePasswd;
	}
	
	public int getTcpPort(){
		return this.tcp_port;
	}
	
	public boolean useSsl(){
		return this.use_ssl;
	}
}