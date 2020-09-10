package scw.logger;

public enum Levels {
	ALL(Level.ALL), TRACE(Level.TRACE), DEBUG(Level.DEBUG), INFO(Level.INFO), WARN(Level.WARN), ERROR(Level.ERROR), OFF(
			Level.OFF);

	private final Level value;

	private Levels(Level value) {
		this.value = value;
	}

	public Level getValue() {
		return value;
	}
}
