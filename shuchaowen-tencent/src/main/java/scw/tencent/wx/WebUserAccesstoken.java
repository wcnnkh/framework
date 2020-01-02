package scw.tencent.wx;

import scw.json.JsonObject;

/**
 * 用code换取的信息
 * 
 * @author shuchaowen
 *
 */
public final class WebUserAccesstoken extends AccessToken {
	private static final long serialVersionUID = 1L;
	private String refresh_token;
	private String openid;
	private String scope;

	/**
	 * 用于序列化
	 */
	WebUserAccesstoken() {
		super(null);
	};

	public WebUserAccesstoken(JsonObject json) {
		super(json);
		if (isSuccess()) {
			this.refresh_token = json.getString("refresh_token");
			this.openid = json.getString("openid");
			this.scope = json.getString("scope");
		}
	}

	public String getRefresh_token() {
		return refresh_token;
	}

	public String getOpenid() {
		return openid;
	}

	public String getScope() {
		return scope;
	}

}
