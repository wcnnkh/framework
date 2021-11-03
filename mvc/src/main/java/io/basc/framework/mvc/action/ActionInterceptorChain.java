package io.basc.framework.mvc.action;

import java.util.Iterator;

import io.basc.framework.mvc.HttpChannel;

public final class ActionInterceptorChain {
	private Iterator<? extends ActionInterceptor> iterator;
	private ActionInterceptorChain next;

	public ActionInterceptorChain(Iterator<? extends ActionInterceptor> iterator) {
		this(iterator, null);
	}

	public ActionInterceptorChain(Iterator<? extends ActionInterceptor> iterator, ActionInterceptorChain next) {
		this.iterator = iterator;
		this.next = next;
	}

	public Object intercept(HttpChannel httpChannel, Action action, ActionParameters parameters) throws Throwable {
		ActionInterceptor actionInterceptor = getNextActionInterceptor(httpChannel, action, parameters);
		if (actionInterceptor == null) {
			if (next == null) {
				Object[] args = parameters.getParameters(httpChannel, action);
				return action.invoke(args);
			} else {
				return next.intercept(httpChannel, action, parameters);
			}
		} else {
			return actionInterceptor.intercept(httpChannel, action, parameters, this);
		}
	}

	private ActionInterceptor getNextActionInterceptor(HttpChannel httpChannel, Action action, ActionParameters parameters) {
		if (!hasNext()) {
			return null;
		}

		ActionInterceptor actionInterceptor = iterator.next();
		if (actionInterceptor instanceof ActionInterceptorAccept) {
			if (((ActionInterceptorAccept) actionInterceptor).isAccept(httpChannel, action, parameters)) {
				return actionInterceptor;
			} else {
				return getNextActionInterceptor(httpChannel, action, parameters);
			}
		}
		return actionInterceptor;
	}

	private boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}

	public boolean isLast() {
		if (hasNext()) {
			return false;
		}

		if (next != null && next.hasNext()) {
			return false;
		}

		return true;
	}
}