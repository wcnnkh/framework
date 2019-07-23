package scw.result;

import scw.transaction.RollbackOnlyResult;

public interface Result extends RollbackOnlyResult{
	int getCode();

	String getMsg();

	boolean isSuccess();
	
	boolean isError();
}
