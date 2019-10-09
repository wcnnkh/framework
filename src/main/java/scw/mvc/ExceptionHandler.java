package scw.mvc;

public interface ExceptionHandler {
	Object handler(Channel channel, Throwable throwable, ExceptionHandlerChain chain);
}
