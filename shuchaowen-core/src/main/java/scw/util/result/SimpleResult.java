package scw.util.result;

import java.io.Serializable;

import scw.lang.Ignore;

@Ignore
public class SimpleResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean success;
	private final T data;

	public SimpleResult(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	public boolean isError() {
		return !isSuccess();
	}
}
