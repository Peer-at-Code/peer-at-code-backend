package be.jeffcheasey88.peeratcode.routes;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.repository.DatabaseRepository;
import be.jeffcheasey88.peeratcode.webserver.HttpReader;
import be.jeffcheasey88.peeratcode.webserver.HttpUtil;
import be.jeffcheasey88.peeratcode.webserver.HttpWriter;
import be.jeffcheasey88.peeratcode.webserver.Response;
import be.jeffcheasey88.peeratcode.webserver.User;

public class PuzzleResponse implements Response {
	private final DatabaseRepository databaseRepo;

	public PuzzleResponse(DatabaseRepository databaseRepo) {
		this.databaseRepo = databaseRepo;
	}

	@Override
	public void exec(Matcher matcher, User user, HttpReader reader, HttpWriter writer) throws Exception {
		HttpUtil.skipHeaders(reader);
		
		int puzzleId = Integer.parseInt(matcher.group(1));
		byte[] response;
		byte[] fileName;
		byte[] sourceCode;

		// Response
		int hSize = reader.readInt();
		response = new byte[hSize];
		if (hSize == reader.read(response)) {
			// File Name
			hSize = reader.readInt();
			fileName = new byte[hSize];
			if (hSize == reader.read(fileName)) {
				// Source Code
				hSize = reader.readInt();
				sourceCode = new byte[hSize];
				if (hSize == reader.read(sourceCode)) {
					int score = databaseRepo.insertOrUpdatePuzzleResponse(puzzleId, "Pseudo", fileName.toString(), sourceCode);
					if (Arrays.equals(response, databaseRepo.getPuzzleSolution(puzzleId))) {
						HttpUtil.responseHeaders(writer, 200,
								"Access-Control-Allow-Origin: *",
								"Content-Type: application/json");
						JSONObject responseJSON = new JSONObject();
						responseJSON.put("id", puzzleId);
						responseJSON.put("score", score);
						writer.write(responseJSON.toJSONString());
						writer.flush();
						writer.close();
					} else {
						HttpUtil.responseHeaders(writer, 406, "Access-Control-Allow-Origin: *");
					}
				}
				else {
					HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
				}
			}
			else {
				HttpUtil.responseHeaders(writer, 400, "Access-Control-Allow-Origin: *");
			}

		}
		HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
	}

	@Override
	public Pattern getPattern() {
		return Pattern.compile("^\\/puzzleResponse\\/([0-9]+)$");
	}

	@Override
	public String getType() {
		return "POST";
	}
}
