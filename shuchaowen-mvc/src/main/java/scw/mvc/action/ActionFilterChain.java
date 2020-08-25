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
		ActionFilter actionFilter = getNextFilter(httpChannel, action, args);
		if (actionFilter == null) {
			if (nextActionFilterChain == null) {
				return action.invoke(args);
			} else {
				return nextActionFilterChain.doFilter(httpChannel, action, args);
			}
		} else {
			return actionFilter.doFilter(httpChannel, action, args, this);
		}
	}

	private ActionFilter getNextFilter(HttpChannel httpChannel, Action action, Object[] args) {
		if (!hasNext()) {
			return null;
		}

		ActionFilter actionFilter = iterator.next();
		if (actionFilter instanceof ActionFilterAccept) {
			if (((ActionFilterAccept) actionFilter).isAccept(httpChannel, action, args)) {
				return actionFilter;
			} else {
				return getNextFilter(httpChannel, action, args);
			}
		}
		return actionFilter;
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