package scw.http.client.accessor;

import scw.http.HttpUtils;

public class HttpClientConfigAccessor {
	private Integer connectTimeout;

	private Integer readTimeout;
	
	public int getConnectTimeout() {
		return connectTimeout == null? HttpUtils.DEFAULT_CONNECT_TIMEOUT.getValue():connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout == null? HttpUtils.DEFAULT_READ_TIMEOUT.getValue():readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public void setConfig(HttpClientConfigAccessor httpClientConfigAccessor){
		setReadTimeout(httpClientConfigAccessor.getReadTimeout());
		setConnectTimeout(httpClientConfigAccessor.getConnectTimeout());
	}
}
