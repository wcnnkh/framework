package scw.core.instance.definition;

import scw.core.instance.InstanceFactory;

public class InstanceFactoryAccessor implements InstanceFactoryAware {
	private transient InstanceFactory instanceFactory;

	public InstanceFactory getInstanceFactory() {
		return instanceFactory;
	}

	public void setInstanceFactory(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}
}
