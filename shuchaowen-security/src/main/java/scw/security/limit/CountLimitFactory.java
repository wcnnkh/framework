package scw.security.limit;

import java.util.concurrent.TimeUnit;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultCountLimitFactory.class })
public interface CountLimitFactory {
	long incrAndGet(String name, long timeout, TimeUnit timeUnit);
}
