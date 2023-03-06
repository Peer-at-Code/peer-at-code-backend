package be.jeffcheasey88.peeratcode.routes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;

import be.jeffcheasey88.peeratcode.model.Completion;
import be.jeffcheasey88.peeratcode.model.Player;
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
		if (user == null) {
			HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
			return;
		}
		ReceivedResponse received = new ReceivedResponse(matcher, reader);
		saveSourceCode(received, databaseRepo.getPlayer(user.getId()));

		JSONObject responseJSON = new JSONObject();
		Completion completion = databaseRepo.insertOrUpdatePuzzleResponse(received.getPuzzleId(), 3,
				received.getFileName(), received.getSourceCode());
		if (Arrays.equals(received.getResponse(), databaseRepo.getPuzzle(received.getPuzzleId()).getSoluce())) {
			HttpUtil.responseHeaders(writer, 200, "Access-Control-Allow-Origin: *", "Content-Type: application/json");
			responseJSON.put("score", completion.getScore());
			responseJSON.put("tries", completion.getTries());

		} else if (completion != null) {
			HttpUtil.responseHeaders(writer, 406, "Access-Control-Allow-Origin: *", "Content-Type: application/json");
			responseJSON.put("tries", completion.getTries());
		} else {
			HttpUtil.responseHeaders(writer, 403, "Access-Control-Allow-Origin: *");
			return;
		}
		writer.write(responseJSON.toJSONString());
	}

	private void saveSourceCode(ReceivedResponse received, Player player) throws IOException {
		Path path = Paths.get(String.format("%s/puz%04d-%s", player.getPathToSourceCode(), received.getPuzzleId(), received.getFileName()));
		Files.write(path, received.getSourceCode());
		
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

class ReceivedResponse {
	
	private int puzzleId;
	private byte[] response;
	private String fileName;
	private byte[] sourceCode;

	private HttpReader reader;

	public ReceivedResponse(Matcher matcher, HttpReader reader) throws Exception {
		this.reader = reader;
		puzzleId = Integer.parseInt(matcher.group(1));
		
		List<String> multiPartData = HttpUtil.readMultiPartData(reader);
		this.response = multiPartData.get(0).getBytes();
		this.fileName = multiPartData.get(1);
		this.sourceCode = multiPartData.get(2).getBytes();
	}

	public int getPuzzleId() {
		return puzzleId;
	}

	public byte[] getResponse() {
		return response;
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getSourceCode() {
		return sourceCode;
	}
}
