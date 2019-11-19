package scw.result;

public interface Result {
	boolean isError();

	int getCode();

	boolean isSuccess();

	String getMsg();
}
