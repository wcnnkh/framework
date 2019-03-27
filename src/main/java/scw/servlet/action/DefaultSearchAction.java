package scw.servlet.action;

import java.util.Collection;

import scw.beans.BeanFactory;
import scw.servlet.Action;
import scw.servlet.Request;

public class DefaultSearchAction implements SearchAction {
	private PathSearchAction pathSearchAction;
	private PathAndParamSearchAction pathAndParamSearchAction;
	private RestSearchAction restSearchAction;

	public DefaultSearchAction(BeanFactory beanFactory, String actionKey) {
		this.pathSearchAction = new PathSearchAction(beanFactory);
		this.pathAndParamSearchAction = new PathAndParamSearchAction(beanFactory, actionKey);
		this.restSearchAction = new RestSearchAction(beanFactory);
	}

	public void init(Collection<Class<?>> classList) throws Exception {
		pathSearchAction.init(classList);
		pathAndParamSearchAction.init(classList);
		restSearchAction.init(classList);
	}

	public Action getAction(Request request) throws Exception {
		Action action = pathAndParamSearchAction.getAction(request);
		if (action == null) {
			action = pathSearchAction.getAction(request);
		}

		if (action == null) {
			action = restSearchAction.getAction(request);
		}
		return action;
	}
}
