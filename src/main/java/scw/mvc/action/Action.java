package scw.mvc.action;

import scw.mvc.Channel;

public interface Action<T extends Channel> {
	Object doAction(T channel) throws Throwable;
}
