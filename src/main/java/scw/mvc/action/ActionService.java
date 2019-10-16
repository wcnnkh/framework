package scw.mvc.action;

import scw.mvc.Channel;
import scw.mvc.Filter;

public interface ActionService extends Filter {
	Action<Channel> getAction(Channel channel);
}
