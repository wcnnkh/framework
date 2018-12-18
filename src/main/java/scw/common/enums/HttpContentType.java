package scw.common.enums;

public enum HttpContentType {
	TEXT_HTML("text/html"),
	TEXT_PLAIN("text/plain"),
	JSON("application/json"),
	FORM("application/x-www-form-urlencoded"),
	FORM_DATA("multipart/form-data"),
	TEXT_JAVASCRIPT("text/javascript"),
	;
	
	private String value;
	HttpContentType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
