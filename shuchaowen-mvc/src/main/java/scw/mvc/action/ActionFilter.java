package scw.mvc.action;

import scw.beans.annotation.AopEnable;
import scw.mvc.HttpChannel;

@AopEnable(false)
public interface ActionFilter {
	Object doFilter(Action action, HttpChannel httpChannel) throws Throwable;
}
