package scw.util.result;

import java.io.Serializable;

public class Result implements Serializable {
	private static final long serialVersionUID = 1L;
	private final String code;
	private final String msg;
	private final boolean success;

	public Result(boolean success, String code, String msg) {
		this.code = code;
		this.success = success;
		this.msg = msg;
	}

	public String getCode() {
		return code;
	}
	
	public Integer getIntegerCode(){
		return Integer.valueOf(getCode());
	}

	public String getMsg() {
		return msg;
	}

	public boolean isSuccess() {
		return success;
	}

	public boolean isError() {
		return !isSuccess();
	}
}
