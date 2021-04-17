package scw.logger;

public enum Level {
	ALL(java.util.logging.Level.ALL),
	TRACE(CustomLevel.TRACE),
	DEBUG(CustomLevel.DEBUG),
	INFO(java.util.logging.Level.INFO),
	WARN(CustomLevel.WARN),
	ERROR(CustomLevel.ERROR), 
	OFF(java.util.logging.Level.OFF),
	;

	private final java.util.logging.Level value;

	private Level(java.util.logging.Level value) {
		this.value = value;
	}

	public java.util.logging.Level getValue() {
		return value;
	}
}
