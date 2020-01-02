package scw.result.support;

import java.io.Serializable;

import scw.result.Result;

public abstract class AbstractResult implements Result, Serializable {
	private static final long serialVersionUID = 1L;

	public boolean isError() {
		return !isSuccess();
	}
}
