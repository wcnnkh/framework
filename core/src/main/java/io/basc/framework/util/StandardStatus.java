package io.basc.framework.util;

import io.basc.framework.lang.Nullable;

public class StandardStatus implements Status {
	private static final long serialVersionUID = 1L;
	private final boolean success;
	private final long code;
	private final String msg;

	public StandardStatus(boolean success, long code, @Nullable String msg) {
		this.success = success;
		this.code = code;
		this.msg = msg;
	}

	public StandardStatus(Status status) {
		if (status instanceof StandardStatus) {
			StandardStatus defaultStatus = (StandardStatus) status;
			this.success = defaultStatus.success;
			this.code = defaultStatus.code;
			this.msg = defaultStatus.msg;
		} else {
			this.success = status.isSuccess();
			this.code = status.getCode();
			this.msg = status.getMsg();
		}
	}

	@Override
	public boolean isSuccess() {
		return success;
	}

	@Override
	public long getCode() {
		return this.code;
	}

	@Override
	public String getMsg() {
		return this.msg;
	}

}
