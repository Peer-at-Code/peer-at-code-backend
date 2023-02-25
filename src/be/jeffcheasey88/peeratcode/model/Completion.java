package be.jeffcheasey88.peeratcode.model;

public class Completion {
	private int puzzleId;
	private int playerId;
	private int tries;
	private String fileName;
	private byte[] code;
	private int score;

	public Completion(int playerId, int puzzleId, String fileName, int score) {
		this(playerId, puzzleId, -1, 1, fileName, score, null);
	}
	
	public Completion(int playerId, int puzzleId, int idCompletion, int tries, String fileName, int score) {
		this(playerId, puzzleId, idCompletion, tries, fileName, score, null);
	}

	public Completion(int playerId, int puzzleId, int idCompletion, int tries, String fileName, int score,
			byte[] file) {
		this.playerId = playerId;
		this.puzzleId = puzzleId;
		this.tries = tries;
		this.fileName = fileName;
		this.score = score;
		this.code = file;
	}
	
	public int getPuzzleId() {
		return puzzleId;
	}

	public int getPlayerId() {
		return playerId;
	}

	public int getTries() {
		return tries;
	}

	public void addTry() {
		this.tries++;
		updateScore();
	}
	private void updateScore() {
		if (tries > 1) {
			score = score * (1-((tries-1)/10));
		}
	}

	public String getFileName() {
		return fileName;
	}

	public byte[] getCode() {
		return code;
	}

	public int getScore() {
		return score;
	}
}
