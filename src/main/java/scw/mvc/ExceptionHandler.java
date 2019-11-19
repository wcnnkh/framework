package scw.mvc;

public interface ExceptionHandler {
	Object handler(Channel channel, Throwable error, ExceptionHandlerChain chain);
}
