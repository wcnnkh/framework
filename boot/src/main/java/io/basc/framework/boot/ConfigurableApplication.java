package io.basc.framework.boot;

import io.basc.framework.context.ConfigurableContext;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;

public interface ConfigurableApplication extends Application, ConfigurableContext {

	default void setPort(int port) {
		getProperties().put(APPLICATION_PORT_PROPERTY, port);
	}

	default void setName(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		getProperties().put(APPLICATION_NAME_PROPERTY, name);
	}
}
