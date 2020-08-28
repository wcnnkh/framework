package scw.net;

import scw.core.utils.StringUtils;

public enum ProtocolType {
	HTTP("http://"),
	HTTPS("https://"),
	/**
	 * 自动识别当前网页是https还是http
	 */
	HTTP_AUTO("//"),
	;
	
	private final String value;
	ProtocolType(String value){
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	
	public boolean isProtocolType(String url){
		return url == null? false: url.startsWith(value);
	}
	
	public static ProtocolType getHttpProtocolType(String url){
		if(StringUtils.isNull(url)){
			return null;
		}
		
		for (ProtocolType protocolType : ProtocolType.values()) {
			if (url.startsWith(protocolType.getValue())) {
				return protocolType;
			}
		}
		return null;
	}
}
