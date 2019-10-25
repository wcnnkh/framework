package scw.mvc.limit;

import scw.beans.annotation.AutoImpl;
import scw.mvc.Channel;
import scw.mvc.action.MethodAction;
import scw.security.limit.CountLimitConfig;

@AutoImpl({ DefaultCountLimitConfigFactory.class })
public interface CountLimitConfigFactory {
	CountLimitConfig getCountLimitConfig(MethodAction action, Channel channel);
}
