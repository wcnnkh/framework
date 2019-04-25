package scw.core;

import java.io.Serializable;

/**
 * 接收程序处理结果 code为0是说明是成功的
 * 
 * @author shuchaowen
 *
 */
public final class ProcessResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private int code;
	private String msg;
	private T data;

	public ProcessResult() {
	};

	public ProcessResult(int code, String msg) {
	};

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public boolean isSuccess() {
		return code == 0;
	}

	public boolean isError() {
		return code != 0;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public static <T> ProcessResult<T> success(T data) {
		ProcessResult<T> result = new ProcessResult<T>();
		result.setCode(0);
		result.setData(data);
		return result;
	}

	public static <T> ProcessResult<T> success() {
		ProcessResult<T> result = new ProcessResult<T>();
		result.setCode(0);
		return result;
	}

	public static <T> ProcessResult<T> error(int code, String msg) {
		ProcessResult<T> result = new ProcessResult<T>();
		result.setCode(code);
		result.setMsg(msg);
		return result;
	}

	public static <T> ProcessResult<T> simpleError(String msg) {
		ProcessResult<T> result = new ProcessResult<T>();
		result.setCode(1);
		result.setMsg(msg);
		return result;
	}
}
