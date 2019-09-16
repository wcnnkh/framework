package scw.result.exception;

import scw.result.Result;

public interface ExceptionFactory {
	Result getResult(ResultException exception);
}
