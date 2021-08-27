package io.basc.framework.boot;

import io.basc.framework.beans.BeanFactory;
import io.basc.framework.context.Context;
import io.basc.framework.context.Destroy;
import io.basc.framework.context.Init;
import io.basc.framework.event.EventDispatcher;
import io.basc.framework.logger.Logger;

public interface Application extends Context, Init, Destroy, EventDispatcher<ApplicationEvent> {
	BeanFactory getBeanFactory();

	Logger getLogger();

	long getCreateTime();
}
