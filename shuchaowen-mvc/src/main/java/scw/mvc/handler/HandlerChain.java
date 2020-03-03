package scw.mvc.handler;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;

@AutoImpl(ConfigurationHandlerChain.class)
public interface HandlerChain{
	void doHandler(Channel channel) throws Throwable;
}
