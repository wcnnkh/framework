package scw.context.support;

import scw.instance.NoArgsInstanceFactory;

public class DefaultContext extends AbstractConfigurableContext {
	private final NoArgsInstanceFactory instanceFactory;

	public DefaultContext(boolean cache, NoArgsInstanceFactory instanceFactory) {
		super(cache);
		this.instanceFactory = instanceFactory;
	}

	@Override
	protected NoArgsInstanceFactory getTargetInstanceFactory() {
		return instanceFactory;
	}

}
