package scw.integration.app.enums;

public enum StatusType {
	available(1, "可用的"), unavailable(0, "不可用的");

	private final int value;
	private final String describe;

	StatusType(int value, String describe) {
		this.value = value;
		this.describe = describe;
	}

	public int getValue() {
		return value;
	}

	public String getDescribe() {
		return describe;
	}

	public static StatusType forValue(Integer value) {
		if (value == null) {
			return null;
		}

		if (value == available.getValue()) {
			return StatusType.available;
		} else if (value == unavailable.getValue()) {
			return StatusType.unavailable;
		} else {
			return null;
		}
	}
}
