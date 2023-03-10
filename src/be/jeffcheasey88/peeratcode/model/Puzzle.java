package be.jeffcheasey88.peeratcode.model;

public class Puzzle {
	
	private int id;
	private String name;
	private String content;
	private byte[] soluce;
	private String verify;
	private int scoreMax;
	private int depend;

	public Puzzle(int id, String name, String content, byte[] soluce, String verify, int scoreMax){
		this(id, name, content, soluce, verify, scoreMax, -1);	
	}
	public Puzzle(int id, String name, String content, byte[] soluce, String verify, int scoreMax, int depend){
		this.id = id;
		this.name = name;
		this.content = content;
		this.soluce = soluce;
		this.verify = verify;
		this.scoreMax = scoreMax;
		this.depend = depend;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public byte[] getSoluce(){
		return this.soluce;
	}
	
	public void setSoluce(byte[] array){
		this.soluce = array;
	}
	
	public String getVerify(){
		return this.verify;
	}
	
	public void setVerify(String regex){
		this.verify = regex;
	}
	
	public int getScoreMax(){
		return this.scoreMax;
	}
	
	public void setScoreMax(int max){
		this.scoreMax = max;
	}
	
	public int getDepend(){
		return this.depend;
	}
	
	public void setDepend(int depend){
		this.depend = depend;
	}

	@Override
	public boolean equals(Object object) {
		if(this == object) return true;
		if(!(object instanceof Puzzle)) return false;
		return this.id == (((Puzzle)object).id);
	}

	@Override
	public int hashCode() {
		return id;
	}
}
