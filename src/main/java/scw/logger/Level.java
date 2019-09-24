package scw.logger;

public enum Level {
	TRACE(8), 
	DEBUG(16), 
	INFO(32),
	WARN(64), 
	ERROR(128);

	private final int level;

	private Level(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}

	public boolean enabled(Level level) {
		return this.level >= level.getLevel();
	}
}
