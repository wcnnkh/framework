package shuchaowen.web.servlet.view.common.enums;

public enum Code {
	login_status_expired(-1, "Your login status has expired or you have logged in elsewhere"),
	success(0, "SUCCESS"),
	error(1, "ERROR"),
	;
	private final int code;
	private final String msg;
	
	private Code(int code, String msg){
		this.code = code;
		this.msg = msg;
	}
	
	public int getCode() {
		return code;
	}
	public String getMsg() {
		return msg;
	}
}
