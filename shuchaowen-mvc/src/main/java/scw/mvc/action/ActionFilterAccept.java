package scw.mvc.action;

import scw.mvc.HttpChannel;

public interface ActionFilterAccept {
	boolean isAccept(HttpChannel httpChannel, Action action, Object[] args);
}
