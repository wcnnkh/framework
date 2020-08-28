package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionInterceptorAccept {
	boolean isAccept(HttpChannel httpChannel, Action action, ActionParameters parameters);
}
