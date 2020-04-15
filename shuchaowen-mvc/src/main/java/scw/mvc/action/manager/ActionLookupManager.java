package scw.mvc.action.manager;

import scw.beans.BeanFactory;
import scw.core.Init;
import scw.core.instance.InstanceUtils;
import scw.mvc.Channel;
import scw.mvc.action.Action;
import scw.util.value.property.PropertyFactory;

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

	public Action lookup(Channel channel) {
		return getMultiActionLookup().lookup(channel);
	}
}
