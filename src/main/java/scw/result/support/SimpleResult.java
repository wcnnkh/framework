package scw.result.support;

import scw.result.Result;

/**
 * 简单的结果处理 code=0表示成功
 * 
 * @author shuchaowen
 *
 */
public final class SimpleResult implements Result {
	private int code;
	private String msg;

	/**
	 * 成功
	 */
	public SimpleResult() {
		this(0, "SUCCESS");
	}

	/**
	 * 失败
	 * 
	 * @param msg
	 */
	public SimpleResult(String msg) {
		this(-1, msg);
	}

	/**
	 * code=0表示成功，其它表示失败
	 * 
	 * @param code
	 * @param msg
	 */
	public SimpleResult(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public boolean isError() {
		return code != 0;
	}

	public int getCode() {
		return 0;
	}

	public boolean isSuccess() {
		return code == 0;
	}

	public String getMsg() {
		return msg;
	}

}
