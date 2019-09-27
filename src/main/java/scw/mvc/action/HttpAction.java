package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Action;
import scw.mvc.Channel;

public interface HttpAction extends Action<Channel> {
	Collection<HttpControllerConfig> getControllerConfigs();
}
