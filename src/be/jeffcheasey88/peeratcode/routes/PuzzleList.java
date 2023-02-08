package be.jeffcheasey88.peeratcode.routes;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;

public class PuzzleList implements Response{
	
	private Connection con;
	
	public PuzzleList(Connection con){
		this.con = con;
	}

	@Override
	public void exec(Matcher matcher, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *");
		
		JSONObject json = new JSONObject();
		JSONArray chapters = new JSONArray();
		
		PreparedStatement chapterStmt = con.prepareStatement("SELECT * FROM chapters");
		ResultSet chapterResults = chapterStmt.executeQuery();
		while(chapterResults.next()){
			JSONObject chapter = new JSONObject();
			chapter.put("id", chapterResults.getString("id_chapter"));
			chapter.put("name", chapterResults.getString("name"));
			chapters.add(chapter);
		}
		json.put("chapters", chapters);
		
		JSONArray puzzles = new JSONArray();
		
		PreparedStatement puzzleStmt = con.prepareStatement("SELECT * FROM puzzles");
		ResultSet puzzleResults = puzzleStmt.executeQuery();
		while(puzzleResults.next()){	
			JSONObject puzzle = new JSONObject();
			puzzle.put("id", puzzleResults.getString("id_puzzle"));
			puzzle.put("name", puzzleResults.getString("name"));
			puzzle.put("content", puzzleResults.getString("content"));
			puzzle.put("chapter", puzzleResults.getString("fk_chapter"));
			puzzles.add(puzzle);
		}
		json.put("puzzles", puzzles);
		
		writer.write(json.toJSONString());
		writer.flush();
		writer.close();
	}

	@Override
	public Pattern getPattern(){
		return Pattern.compile("^\\/puzzle\\/?$");
	}

}
