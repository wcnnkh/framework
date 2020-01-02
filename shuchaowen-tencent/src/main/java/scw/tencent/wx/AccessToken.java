package scw.tencent.wx;

import scw.json.JsonObject;

public class AccessToken extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String access_token;
	private int expires_in;
	private long cts;// 创建时间

	/**
	 * 用于序列化
	 */
	AccessToken() {
		super(null);
	};

	public AccessToken(JsonObject json) {
		super(json);
		this.cts = System.currentTimeMillis();
		this.access_token = json.getString("access_token");
		this.expires_in = json.getIntValue("expires_in");
	}

	public String getAccess_token() {
		return access_token;
	}

	public int getExpires_in() {
		return expires_in;
	}

	public long getCts() {
		return cts;
	}

	// 判断是否已经过期 提前5分钟过期
	public boolean isExpires() {
		return (System.currentTimeMillis() - cts) > (expires_in - 300) * 1000L;
	}
}
