package scw.mvc.handler;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;

@AutoImpl(ConfigurationHandlerChain.class)
public interface HandlerChain{
	Object doHandler(Channel channel) throws Throwable;
}
