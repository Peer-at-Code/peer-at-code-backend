package be.jeffcheasey88.peeratcode.model;

public class Badge {
	private String name;
	private byte[] logo;
	private int level;

	public Badge(String name, int level) {
		this(name, null, level);
	}

	public Badge(String name, byte[] logo, int level) {
		this.name = name;
		this.logo = logo;
		this.level = level;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

}
