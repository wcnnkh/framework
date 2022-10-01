package io.basc.framework.boot;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.Context;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.Init;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.util.Named;

public interface Application extends Context, Init, Destroy, Named {
	public static final String APPLICATION_NAME_PROPERTY = "application.name";
	public static final String APPLICATION_PORT_PROPERTY = "application.port";
	/**
	 * 默认端口号:8080
	 */
	public static final int DEFAULT_PORT = Integer.getInteger("io.basc.framework.application.port.default", 8080);

	EventDispatcher<ApplicationEvent> getEventDispatcher();

	Logger getLogger();

	long getCreateTime();

	ClassesLoader getSourceClasses();

	@Nullable
	@Override
	default String getName() {
		return getProperties().getString(APPLICATION_NAME_PROPERTY);
	}

	/**
	 * -1说明不存在
	 * 
	 * @return
	 */
	default int getPort() {
		return getProperties().getValue(APPLICATION_PORT_PROPERTY, Integer.class, -1);
	}
}
