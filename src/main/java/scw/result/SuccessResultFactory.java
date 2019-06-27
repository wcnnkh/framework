package scw.result;

public interface SuccessResultFactory {
	<T> DataResult<T> success();

	<T> DataResult<T> success(T data);
}
