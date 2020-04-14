package scw.mvc.action.logger;

import scw.mvc.Channel;
import scw.mvc.action.Action;

public interface ActionLogFactory {
	ActionLog createActionLog(Action action, Channel channel, Object response, Throwable error);
}
