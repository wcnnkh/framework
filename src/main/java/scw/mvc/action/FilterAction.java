package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Action;
import scw.mvc.Channel;

public interface FilterAction extends Action<Channel> {
	Collection<ActionFilter> getActionFilters();
}
