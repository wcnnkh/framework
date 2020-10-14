package scw.apple.pay;

import scw.json.JsonObject;

public class ApplePayDate {
	private JsonObject jsonObject;
	private String name;

	public ApplePayDate(JsonObject jsonObject, String name) {
		this.jsonObject = jsonObject;
		this.name = name;
	}

	/**
	 * 日期时间格式类似于ISO8601
	 * 
	 * @return
	 */
	public String getDate() {
		return jsonObject.getString(name);
	}

	/**
	 * UNIX纪元时间格式，以毫秒为单位, 使用此时间格式来处理日期
	 * 
	 * @return
	 */
	public long getMs() {
		return jsonObject.getLongValue(name + "_ms");
	}

	/**
	 * 位于太平洋时区
	 * 
	 * @return
	 */
	public String getPst() {
		return jsonObject.getString(name + "_pst");
	}
}
