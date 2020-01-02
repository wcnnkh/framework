package scw.util;

public class SimplePhoneNumber extends AbstractPhoneNumber {
	private static final long serialVersionUID = 1L;
	private String areaCode;
	private String number;
	private String connector;

	public SimplePhoneNumber(String number) {
		this(DEFAULT_AREA_CODE, number);
	}

	public SimplePhoneNumber(String areaCode, String number) {
		this(areaCode, number, DEFAULT_CONNECTOR);
	}

	public SimplePhoneNumber(String areaCode, String number, String connector) {
		this.areaCode = areaCode;
		this.number = number;
		this.connector = connector;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public String getNumber() {
		return number;
	}

	@Override
	public String getConnector() {
		return connector;
	}
}
