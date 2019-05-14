package scw.result;

public interface SuccessResultFactory {
	<T> DataResult<T> success();

	<T> DataResult<T> success(T data);

	<T> DataResult<T> success(int code, T data, String msg);

}
