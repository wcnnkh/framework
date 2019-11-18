package scw.result.support;

import scw.result.BaseDataResult;

public class DefaultBaseDataResult<T> extends DefaultBaseResult implements
		BaseDataResult<T> {
	private T data;

	private DefaultBaseDataResult() {
		super(true, null);
	}

	public DefaultBaseDataResult(boolean success, String msg, T data) {
		super(success, msg);
		this.data = data;
	}

	public T getData() {
		return data;
	}

}
