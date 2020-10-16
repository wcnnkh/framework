package scw.tencent.qq.connect;

import java.io.Serializable;

public class QQRequest implements Serializable{
	private static final long serialVersionUID = 1L;
	private final String accessToken;
	private final String openid;

	public QQRequest(String accessToken, String openid) {
		this.accessToken = accessToken;
		this.openid = openid;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getOpenid() {
		return openid;
	}
}
