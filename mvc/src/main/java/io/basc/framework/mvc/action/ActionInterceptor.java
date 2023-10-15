wpackage io.basc.framework.mvc.action;

import io.basc.framework.mvc.HttpChannel;

public interface ActionInterceptor {
	Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters, ActionInterceptorChain chain)
			throws Throwable;
}