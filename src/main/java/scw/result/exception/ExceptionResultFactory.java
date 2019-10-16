package scw.result.exception;

import scw.beans.annotation.AutoImpl;
import scw.result.Result;

@AutoImpl(DefaultExceptionResultFactory.class)
public interface ExceptionResultFactory {
	Result error(Throwable e);
}
