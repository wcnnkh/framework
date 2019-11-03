package scw.security.limit;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultCountLimitFactory.class })
public interface CountLimitFactory {
	long incrAndGet(CountLimitConfig countLimitConfig);
}
