package shuchaowen.core.http.enums;

public enum ContentType {
	TEXT_HTML("text/html"),
	TEXT_PLAIN("text/plain"),
	JSON("application/json"),
	FORM("application/x-www-form-urlencoded"),
	FORM_DATA("multipart/form-data"),
	;
	
	private String value;
	ContentType(String value){
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
}
