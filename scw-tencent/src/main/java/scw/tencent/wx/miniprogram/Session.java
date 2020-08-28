package scw.tencent.wx.miniprogram;

import scw.json.JsonObject;
import scw.tencent.wx.BaseResponse;

public final class Session extends BaseResponse {
	private static final long serialVersionUID = 1L;
	private String openid;
	private String session_key;
	private String unionid;

	Session() {
		super(null);
	};

	public Session(JsonObject json) {
		super(json);
		if(isSuccess()){
			this.openid = json.getString("openid");
			this.session_key = json.getString("session_key");
			this.unionid = json.getString("unionid");
		}
	}

	public String getOpenid() {
		return openid;
	}

	public String getSession_key() {
		return session_key;
	}

	public String getUnionid() {
		return unionid;
	}
}
