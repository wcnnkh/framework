package scw.mvc.logger;

import scw.net.http.server.mvc.HttpChannel;
import scw.net.http.server.mvc.action.Action;

public interface ActionLogFactory {
	ActionLog createActionLog(Action action, HttpChannel httpChannel, Object response, Throwable error);
}
