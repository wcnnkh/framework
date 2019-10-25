package scw.mvc.limit;

import scw.beans.annotation.Bean;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.mvc.Channel;
import scw.mvc.action.MethodAction;
import scw.mvc.annotation.CountLimitSecurityConfig;
import scw.mvc.annotation.CountLimitSecurityName;
import scw.security.limit.CountLimitConfig;
import scw.security.limit.SimpleCountLimitConfig;

@Bean(proxy = false)
public class DefaultCountLimitConfigFactory implements CountLimitConfigFactory {
	private static final String DEFAULT_PREFIX = StringUtils
			.toString(SystemPropertyUtils.getProperty("mvc.limit.name.prefix"), "mvc.limit:");

	private final String prefix;

	public DefaultCountLimitConfigFactory() {
		this(DEFAULT_PREFIX);
	}

	public DefaultCountLimitConfigFactory(String prefix) {
		this.prefix = prefix;
	}

	public CountLimitConfig getCountLimitConfig(MethodAction action, Channel channel) {
		CountLimitSecurityConfig config = action.getAnnotation(CountLimitSecurityConfig.class);
		CountLimitSecurityName name = action.getAnnotation(CountLimitSecurityName.class);
		if (config == null || name == null) {
			return null;
		}

		return new SimpleCountLimitConfig((StringUtils.isEmpty(prefix) ? DEFAULT_PREFIX : prefix) + name.value(),
				config.maxCount(), config.period(), config.timeUnit());
	}

}
