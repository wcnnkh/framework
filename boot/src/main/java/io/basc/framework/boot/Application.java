package io.basc.framework.boot;

import io.basc.framework.context.Context;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.factory.Destroy;
import io.basc.framework.factory.Init;
import io.basc.framework.logger.Logger;

public interface Application extends Context, Init, Destroy {
	EventDispatcher<ApplicationEvent> getEventDispatcher();

	Logger getLogger();

	long getCreateTime();
}
