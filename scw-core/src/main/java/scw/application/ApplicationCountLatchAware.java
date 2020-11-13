package scw.application;

import scw.util.concurrent.CountLatch;

public interface ApplicationCountLatchAware {
	void setInitializationCountLatch(CountLatch countLatch);
}
