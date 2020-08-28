package scw.tencent.qq;

import java.io.Serializable;

public class Configuration implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String appId;
	private final String appKey;

	public Configuration(String appId, String appKey) {
		this.appId = appId;
		this.appKey = appKey;
	}

	public String getAppId() {
		return appId;
	}

	public String getAppKey() {
		return appKey;
	}
}
