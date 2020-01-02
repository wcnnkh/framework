package scw.app.common.enums;

public enum SexType {
	undefined(0, "未知"), men(1, "男"), women(2, "女");

	private final int value;
	private final String describe;

	private SexType(int value, String describe) {
		this.value = value;
		this.describe = describe;
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	public String getDescribe() {
		return describe;
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
