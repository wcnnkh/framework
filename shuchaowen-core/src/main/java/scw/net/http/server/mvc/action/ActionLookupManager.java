package scw.net.http.server.mvc.action;

import scw.beans.BeanFactory;
import scw.core.Init;
import scw.core.instance.InstanceUtils;
import scw.net.http.server.mvc.HttpChannel;
import scw.value.property.PropertyFactory;

public class ActionLookupManager implements Init {
	private final MultiActionLookup multiActionLookup = new MultiActionLookup();
	private ActionManager actionManager;

	public ActionLookupManager(BeanFactory beanFactory,
			PropertyFactory propertyFactory) {
		if (beanFactory.isInstance(ActionManager.class)) {
			this.actionManager = beanFactory.getInstance(ActionManager.class);
		}
		multiActionLookup.addAll(InstanceUtils.getConfigurationList(
				ActionLookup.class, beanFactory, propertyFactory));
	}
	
	public void init() throws Exception {
		if (actionManager != null) {
			for (Action action : actionManager.getActions()) {
				multiActionLookup.register(action);
			}
		}
	}

	public MultiActionLookup getMultiActionLookup() {
		return multiActionLookup;
	}

	public Action lookup(HttpChannel httpChannel) {
		return getMultiActionLookup().lookup(httpChannel);
	}
}
