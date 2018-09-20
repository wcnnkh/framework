package shuchaowen.core.http.server.search;

import java.util.Collection;

import shuchaowen.core.beans.BeanFactory;
import shuchaowen.core.http.server.Action;
import shuchaowen.core.http.server.Request;
import shuchaowen.core.http.server.SearchAction;

public class DefaultSearchAction implements SearchAction{
	private PathSearchAction pathSearchAction;
	private PathAndParamSearchAction pathAndParamSearchAction;
	
	public DefaultSearchAction(BeanFactory beanFactory, boolean isResturl, String actionKey) {
		this.pathSearchAction = new PathSearchAction(beanFactory, isResturl);
		this.pathAndParamSearchAction = new PathAndParamSearchAction(beanFactory, actionKey);
	}
	
	public void init(Collection<Class<?>> classList) throws Throwable {
		pathSearchAction.init(classList);
		pathAndParamSearchAction.init(classList);
	}

	public Action getAction(Request request) throws Throwable {
		Action action = pathAndParamSearchAction.getAction(request);
		if(action == null){
			action = pathSearchAction.getAction(request);
		}
		return action;
	}
}
