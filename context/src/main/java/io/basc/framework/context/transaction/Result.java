package io.basc.framework.context.transaction;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StandardStatus;
import io.basc.framework.util.Status;

public class Result extends StandardStatus implements RollbackOnlyResult {
	private static final long serialVersionUID = 1L;

	private final boolean rollbackOnly;

	public Result(boolean success, long code, String msg, boolean rollbackOnly) {
		super(success, code, msg);
		this.rollbackOnly = rollbackOnly;
	}

	public Result(Status status, boolean rollbackOnly) {
		super(status);
		this.rollbackOnly = rollbackOnly;
	}

	@Override
	public boolean isRollbackOnly() {
		return rollbackOnly;
	}

	@Override
	public <T> DataResult<T> toReturn() {
		return toReturn(null);
	}

	@Override
	public <T> DataResult<T> toReturn(T value) {
		return new DataResult<T>(this, rollbackOnly, value);
	}

	static Result success() {
		return success(null);
	}

	static Result success(String msg) {
		return success(0, msg);
	}

	static Result success(long code, String msg) {
		return new Result(true, code, msg, false);
	}

	static Result error(@Nullable String msg) {
		return error(0, msg);
	}

	static Result error(long code, @Nullable String msg) {
		return new Result(false, code, msg, true);
	}
}
