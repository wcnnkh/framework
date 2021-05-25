package scw.boot;

import scw.beans.BeanFactory;
import scw.context.Context;
import scw.context.Destroy;
import scw.context.Init;
import scw.event.EventDispatcher;
import scw.logger.Logger;

public interface Application extends Context, Init, Destroy, EventDispatcher<ApplicationEvent> {
	BeanFactory getBeanFactory();

	Logger getLogger();

	long getCreateTime();
}
