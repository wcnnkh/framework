package io.basc.framework.boot;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.Context;
import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.Init;
import io.basc.framework.logger.Logger;
import io.basc.framework.net.InetUtils;
import io.basc.framework.util.Optional;
import io.basc.framework.util.OptionalInt;

public interface Application extends Context, Init, Destroy {
	public static final String APPLICATION_NAME_PROPERTY = "application.name";
	public static final String APPLICATION_PORT_PROPERTY = "application.port";

	public static final int DEFAULT_PORT = Integer.getInteger("io.basc.framework.application.default.port", 8080);

	BroadcastEventDispatcher<ApplicationEvent> getEventDispatcher();

	Logger getLogger();

	long getCreateTime();

	ClassesLoader getSourceClasses();

	default Optional<String> getName() {
		return getProperties().getObservable(APPLICATION_NAME_PROPERTY).convert((e) -> e.getAsString());
	}

	default OptionalInt getPort() {
		Integer port = getProperties().getAsObject(APPLICATION_PORT_PROPERTY, Integer.class);
		return port == null ? OptionalInt.empty() : OptionalInt.of(port);
	}

	static int getAvailablePort() {
		if (InetUtils.isAvailablePort(DEFAULT_PORT)) {
			return DEFAULT_PORT;
		}
		return InetUtils.getAvailablePort();
	}
}
