package scw.tencent.wx;

import java.io.Serializable;

public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String appId;
	private final String appSecret;

	public Configuration(String appId, String appSecret) {
		this.appId = appId;
		this.appSecret = appSecret;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppSecret() {
		return appSecret;
	}
}
