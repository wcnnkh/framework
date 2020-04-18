package scw.util.result;

public class DataResult<T> extends Result {
	private static final long serialVersionUID = 1L;
	private final T data;

	public DataResult(boolean success, String code, String msg, T data) {
		super(success, code, msg);
		this.data = data;
	}

	public T getData() {
		return data;
	}
}
