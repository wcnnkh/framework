package scw.result;

public interface ErrorResultFactory {
	<T> DataResult<T> error(int code, String msg, T data, boolean rollbackOnly);

	<T> DataResult<T> error(int code, String msg, T data);

	<T> DataResult<T> error(int code, T data, boolean rollbackOnly);

	<T> DataResult<T> error(int code, T data);

	<T> DataResult<T> error(int code, String msg);

	<T> DataResult<T> error(int code);

	<T> DataResult<T> error(String msg, T data, boolean rollbackOnly);

	<T> DataResult<T> error(String msg, T data);

	<T> DataResult<T> error(T data, boolean rollbackOnly);

	<T> DataResult<T> error(T data);

	<T> DataResult<T> error();

	<T> DataResult<T> error(String msg);

	<T> DataResult<T> error(Result result);
}
