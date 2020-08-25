package scw.mvc.action;

import java.util.Iterator;

import scw.mvc.HttpChannel;

public final class ActionFilterChain {
	private Iterator<? extends ActionFilter> iterator;
	private ActionFilterChain nextActionFilterChain;

	public ActionFilterChain(Iterator<? extends ActionFilter> iterator) {
		this(iterator, null);
	}

	public ActionFilterChain(Iterator<? extends ActionFilter> iterator, ActionFilterChain nextActionFilterChain) {
		this.iterator = iterator;
		this.nextActionFilterChain = nextActionFilterChain;
	}

	public Object doFilter(HttpChannel httpChannel, Action action, Object[] args) throws Throwable {
		if (hasNext()) {
			return iterator.next().doFilter(httpChannel, action, args, this);
		} else {
			if (nextActionFilterChain == null) {
				return action.invoke(args);
			} else {
				return nextActionFilterChain.doFilter(httpChannel, action, args);
			}
		}
	}

	private boolean hasNext() {
		return iterator != null && iterator.hasNext();
	}

	public boolean isLast() {
		if (hasNext()) {
			return false;
		}

		if (nextActionFilterChain != null && nextActionFilterChain.hasNext()) {
			return false;
		}

		return true;
	}
}