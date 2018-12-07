package shuchaowen.connection.http.enums;

import shuchaowen.common.utils.StringUtils;

public enum HttpProtocolType {
	HTTP("http://"),
	HTTPS("https://"),
	/**
	 * 此类型只能用于浏览器
	 * 因为浏览器可以自动识别当前网页是https还是http
	 */
	AUTO("//"),
	;
	
	private final String value;
	HttpProtocolType(String value){
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public boolean isProtocolType(String url){
		return url == null? false: url.startsWith(value);
	}
	
	public static HttpProtocolType getHttpProtocolType(String url){
		if(StringUtils.isNull(url)){
			return null;
		}
		
		for (HttpProtocolType protocolType : HttpProtocolType.values()) {
			if (url.startsWith(protocolType.getValue())) {
				return protocolType;
			}
		}
		return null;
	}
}
