package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionService {
	Object doAction(HttpChannel httpChannel, Action action) throws Throwable;
}
