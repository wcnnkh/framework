package scw.orm.support;

import scw.core.GlobalPropertyFactory;

public class DefaultColumnFactory extends CacheColumnFactoryWrapper {

	public DefaultColumnFactory() {
		super(new MethodColumnFactory(GlobalPropertyFactory.getInstance()));
	}

}
