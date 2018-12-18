package scw.servlet.action;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.servlet.Request;

public class DefaultSearchAction implements SearchAction{
	private PathSearchAction pathSearchAction;
	private PathAndParamSearchAction pathAndParamSearchAction;
	
	public DefaultSearchAction(BeanFactory beanFactory, boolean isResturl, String actionKey) {
		this.pathSearchAction = new PathSearchAction(beanFactory, isResturl);
		this.pathAndParamSearchAction = new PathAndParamSearchAction(beanFactory, actionKey);
	}
	
	public void init(Collection<Class<?>> classList) throws Exception {
		pathSearchAction.init(classList);
		pathAndParamSearchAction.init(classList);
	}

	public Action getAction(Request request) throws Exception {
		Action action = pathAndParamSearchAction.getAction(request);
		if(action == null){
			action = pathSearchAction.getAction(request);
		}
		return action;
	}
}
