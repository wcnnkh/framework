package scw.common.enums;

public enum SexType {
	undefined(0), men(1), women(2);

	private final int value;

	private SexType(int value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	public static SexType forValue(int value) {
		for (SexType sexType : values()) {
			if (sexType.getValue() == value) {
				return sexType;
			}
		}
		return null;
	}
}
