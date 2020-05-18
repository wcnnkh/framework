package scw.mvc.logger;

import scw.mvc.HttpChannel;
import scw.mvc.action.Action;

public interface ActionLogFactory {
	ActionLog createActionLog(Action action, HttpChannel httpChannel, Object response, Throwable error);
}
