package scw.result;

public interface Result {
	int getCode();

	String getMsg();

	boolean isSuccess();
	
	boolean isError();
}
