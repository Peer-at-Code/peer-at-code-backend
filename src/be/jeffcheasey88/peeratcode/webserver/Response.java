package be.jeffcheasey88.peeratcode.webserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Response{

	void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception ;
	
	Pattern getPattern();
	
	default String getType(){ return "GET"; }
	
	default boolean needLogin() { return false; }
}