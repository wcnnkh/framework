package shuchaowen.web.servlet.action;

import java.util.Collection;

import shuchaowen.web.servlet.Request;

public interface SearchAction {
	public Action getAction(Request request) throws Throwable;
	
	void init(Collection<Class<?>> classList) throws Throwable;
}
