package scw.util.result;

import java.io.Serializable;

import scw.lang.Ignore;

@Ignore
public class CommonResult<T> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final boolean success;
	private final T data;

	public CommonResult(boolean success) {
		this(success, null);
	}

	public CommonResult(boolean success, T data) {
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
	public static final class AnyResult extends CommonResult<Object> {
		private static final long serialVersionUID = 1L;

		public AnyResult(boolean success) {
			super(success);
		}

		public AnyResult(boolean success, Object data) {
			super(success, data);
		}
	}
}
