package scw.mvc.exception;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;

@AutoImpl(ConfigurationExceptionHandlerChain.class)
public interface ExceptionHandlerChain {
	Object doHandler(Channel channel, Throwable error);
}
