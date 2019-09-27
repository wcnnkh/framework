package scw.mvc.action;

import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.FilterInterface;

public interface ActionFilter extends FilterInterface{
	Object filter(Action<Channel> action, Channel channel, ActionFilterChain chain) throws Throwable;
}
