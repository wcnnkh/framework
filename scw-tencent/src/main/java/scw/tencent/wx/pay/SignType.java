package scw.tencent.wx.pay;

public enum SignType {
	MD5("MD5"), HMAC_SHA256("HMAC-SHA256");

	private final String value;

	private SignType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public static SignType forValue(String value) {
		for (SignType signType : values()) {
			if (signType.value.equalsIgnoreCase(value) || signType.name().equalsIgnoreCase(value)) {
				return signType;
			}
		}
		return null;
	}
}
