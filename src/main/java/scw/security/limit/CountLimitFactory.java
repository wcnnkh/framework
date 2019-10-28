package scw.security.limit;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ DefaultCountLimitFactory.class })
public interface CountLimitFactory {
	CountLimit getCountLimit(CountLimitConfig countLimitConfig);
}
