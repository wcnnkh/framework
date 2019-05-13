package scw.result;

public interface SuccessResultFactory {
	<T extends Result> T success();

	<D, T extends DataResult<? super D>> T success(D data);

	<D, T extends DataResult<? super D>> T success(int code, D data,
			String msg);

}
