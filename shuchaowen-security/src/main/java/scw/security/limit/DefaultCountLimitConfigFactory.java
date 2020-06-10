package scw.security.limit;

import java.lang.reflect.Method;

import scw.beans.annotation.Bean;
import scw.core.Constants;
import scw.core.annotation.AnnotationUtils;
import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.security.limit.annotation.CountLimitParameter;
import scw.security.limit.annotation.CountLimitSecurityCount;
import scw.security.limit.annotation.CountLimitSecurityName;

@Bean(proxy = false)
public class DefaultCountLimitConfigFactory implements CountLimitConfigFactory {

	public CountLimitConfig getCountLimitConfig(Class<?> clazz, Method method,
			Object[] args) throws Throwable {
		CountLimitSecurityCount countLimitSecurityCount = AnnotationUtils
				.getAnnotation(CountLimitSecurityCount.class, clazz, method);
		if (countLimitSecurityCount == null) {
			return null;
		}

		CountLimitSecurityName countLimitSecurityName = AnnotationUtils
				.getAnnotation(CountLimitSecurityName.class, clazz, method);
		if (countLimitSecurityName == null) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		if (Constants.DEFAULT_PREFIX != null) {
			sb.append(Constants.DEFAULT_PREFIX);
		}

		sb.append("mvc.limit:");
		sb.append(countLimitSecurityName.value());
		ParameterDescriptor[] parameterConfigs = ParameterUtils
				.getParameterDescriptors(method);
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterDescriptor config = parameterConfigs[i];
			boolean b = countLimitSecurityName.condition();
			CountLimitParameter countLimitParameter = config
					.getAnnotatedElement().getAnnotation(
							CountLimitParameter.class);
			if (countLimitParameter != null) {
				b = countLimitParameter.value();
			}

			if (b) {
				sb.append("&");
				sb.append(config.getName());
				sb.append("=");
				Object v = args[i];
				if (v == null) {
					sb.append(v);
				} else {
					sb.append((v instanceof CountLimitConfigName) ? ((CountLimitConfigName) v)
							.getCountLimitConfigName() : v);
				}
			}
		}

		return new SimpleCountLimitConfig(sb.toString(),
				countLimitSecurityCount.value(),
				countLimitSecurityCount.period(),
				countLimitSecurityCount.timeUnit());
	}

}
