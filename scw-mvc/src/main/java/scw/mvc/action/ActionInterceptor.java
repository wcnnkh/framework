package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionInterceptor {
	Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters, ActionInterceptorChain chain)
			throws Throwable;
}