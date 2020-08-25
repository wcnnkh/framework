package scw.mvc.action;

import scw.beans.annotation.AopEnable;
import scw.mvc.HttpChannel;

@AopEnable(false)
public interface ActionFilter {
	Object doFilter(HttpChannel httpChannel, Action action, Object[] args, ActionFilterChain filterChain)
			throws Throwable;
}