package scw.http.client.accessor;

import scw.core.GlobalPropertyFactory;
import scw.event.support.DynamicValue;

public class HttpClientConfigAccessor {
	public static final DynamicValue<Integer> DEFAULT_CONNECT_TIMEOUT = GlobalPropertyFactory.getInstance().getDynamicValue("scw.http.client.connect.timeout", Integer.class, 10000);
	public static final DynamicValue<Integer> DEFAULT_READ_TIMEOUT = GlobalPropertyFactory.getInstance().getDynamicValue("scw.http.client.read.timeout", Integer.class, 10000);
	
	private Integer connectTimeout;

	private Integer readTimeout;
	
	public int getConnectTimeout() {
		return connectTimeout == null? DEFAULT_CONNECT_TIMEOUT.getValue():connectTimeout;
	}

	public void setConnectTimeout(Integer connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getReadTimeout() {
		return readTimeout == null? DEFAULT_READ_TIMEOUT.getValue():readTimeout;
	}

	public void setReadTimeout(Integer readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	public void setConfig(HttpClientConfigAccessor httpClientConfigAccessor){
		this.connectTimeout = httpClientConfigAccessor.connectTimeout;
		this.readTimeout = httpClientConfigAccessor.readTimeout;
	}
}
