package be.jeffcheasey88.peeratcode.webserver;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import be.jeffcheasey88.peeratcode.model.User;

public interface Response{

	void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception ;
	
	Pattern getPattern();
	
	default String getType(){ return "GET"; }
}