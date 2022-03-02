package io.basc.framework.data.resource;

import io.basc.framework.http.HttpRequestEntity;

public class ResourceUploadPolicy {
	private final HttpRequestEntity<?> policy;
	private final String url;

	/**
	 * @param url    访问路径
	 * @param policy 策略
	 */
	public ResourceUploadPolicy(String url, HttpRequestEntity<?> policy) {
		this.url = url;
		this.policy = policy;
	}

	public HttpRequestEntity<?> getPolicy() {
		return policy;
	}

	public String getUrl() {
		return url;
	}
}