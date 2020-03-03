package scw.mvc.action.exception;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.Action;

@AutoImpl(ConfigurationActionExceptionHandlerChain.class)
public interface ActionExceptionHandlerChain {
	Object doHandler(Channel channel, Action action, Throwable error);
}
