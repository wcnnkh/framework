package scw.result;

import scw.transaction.RollbackOnlyResult;

public interface Result extends RollbackOnlyResult, BaseResult {
	boolean isError();

	int getCode();
}
