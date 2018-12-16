package shuchaowen.web.servlet.action;

import java.util.Collection;

import shuchaowen.web.servlet.Request;

public interface SearchAction {
	public Action getAction(Request request) throws Exception;
	
	void init(Collection<Class<?>> classList) throws Exception;
}
