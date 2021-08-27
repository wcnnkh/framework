package io.basc.framework.mvc.action;

import io.basc.framework.mvc.HttpChannel;

public interface ActionInterceptorAccept {
	boolean isAccept(HttpChannel httpChannel, Action action, ActionParameters parameters);
}
