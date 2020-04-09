package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.filter.ActionFilter;

public interface FilterAction extends Action{
	Collection<ActionFilter> getFilters();
	
	Object doFilterAction(Channel channel) throws Throwable;
}
