package scw.util;

import java.io.Serializable;

import scw.lang.Ignore;

@Ignore
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean success;
	private final T data;

	// 用于序列化
	@Ignore
	private Result() {
		this(false, null);
	}

	public Result(boolean success) {
		this(success, null);
	}

	public Result(boolean success, T data) {
		this.success = success;
		this.data = data;
	}

	public boolean isSuccess() {
		return success;
	}

	public T getData() {
		return data;
	}

	@Ignore
	public static final class AnyResult extends Result<Object> {
		private static final long serialVersionUID = 1L;

		// 用于序列化
		@Ignore
		private AnyResult() {
			super();
		}

		public AnyResult(boolean success) {
			super(success);
		}

		public AnyResult(boolean success, Object data) {
			super(success, data);
		}
	}
}
