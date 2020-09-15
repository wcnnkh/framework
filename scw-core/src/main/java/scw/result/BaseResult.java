package scw.result;

import java.io.Serializable;

import scw.mapper.MapperUtils;

public class BaseResult implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean success;
	private String msg;

	public BaseResult(boolean success) {
		this.success = success;
	}

	public BaseResult(BaseResult baseResult) {
		this.success = baseResult.success;
		this.msg = baseResult.msg;
	}

	public final boolean isSuccess() {
		return success;
	}

	public String getMsg() {
		return msg;
	}

	public BaseResult setMsg(String msg) {
		this.msg = msg;
		return this;
	}

	public boolean isError() {
		return !isSuccess();
	}

	@Override
	public String toString() {
		return MapperUtils.getMapper().toString(this);
	}
}
