package scw.result;

public interface ErrorResultFactory {
	<T> DataResult<T> error();

	<T> DataResult<T> error(int code, String msg);

	<T> DataResult<T> error(int code);

	<T> DataResult<T> error(String msg);
	
	<T> DataResult<T> error(Result result);
}
