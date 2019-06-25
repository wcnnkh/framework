package scw.beans.async;

public interface AsyncCompleteService {
	Object service(AsyncInvokeInfo asyncInvokeInfo) throws Throwable;
}
