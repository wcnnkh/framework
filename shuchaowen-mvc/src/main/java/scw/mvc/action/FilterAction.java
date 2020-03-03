package scw.mvc.action;

import java.util.Collection;

import scw.mvc.Channel;
import scw.mvc.action.filter.Filter;

public interface FilterAction extends Action{
	Collection<Filter> getFilters();
	
	Object doFilterAction(Channel channel) throws Throwable;
}
