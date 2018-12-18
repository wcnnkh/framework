package scw.common.enums;

public enum StatusType {
	available(1), unavailable(0);

	private int value;

	StatusType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static StatusType forValue(int value) {
		if (value == available.getValue()) {
			return StatusType.available;
		} else if (value == unavailable.getValue()) {
			return StatusType.unavailable;
		} else {
			return null;
		}
	}
}
