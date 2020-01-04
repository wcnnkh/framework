package scw.async.beans;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultAsyncCompleteService.class })
public interface AsyncCompleteService {
	Object service(AsyncInvokeInfo asyncInvokeInfo) throws Throwable;
}
