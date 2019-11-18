package scw.mvc;

public interface ExceptionHandlerChain {
	Object doHandler(Channel channel, Throwable throwable);
}
