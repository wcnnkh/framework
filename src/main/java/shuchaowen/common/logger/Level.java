package shuchaowen.common.logger;

public enum Level {
	TRACE(0),
	DEBUG(1),
	INFO(2),
	WARN(3),
	ERROR(4);
	
	private int value;

	Level(int v) {
		value = v;
	}

	public int getValue() {
		return value;
	}
}
