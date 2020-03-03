package scw.mvc.action.logger;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.Action;

@AutoImpl(scw.mvc.action.logger.DefaultActionLogFactory.class)
public interface ActionLogFactory {
	ActionLog createActionLog(Action action, Channel channel, Object response, Throwable error);
}
