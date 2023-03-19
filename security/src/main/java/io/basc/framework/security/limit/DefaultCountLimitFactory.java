package io.basc.framework.security.limit;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.reflect.MethodInvoker;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.mapper.ParameterUtils;
import io.basc.framework.security.limit.annotation.CountLimitParameter;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;

@Provider(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultCountLimitFactory implements CountLimitFactory {

	public String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("count-limit:");
		sb.append(invoker.getMethod().getName());
		ParameterDescriptor[] parameterConfigs = ParameterUtils.getParameters(invoker.getMethod());
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterDescriptor config = parameterConfigs[i];
			boolean b = countLimitSecurity.useAllParameters();
			CountLimitParameter countLimitParameter = config.getAnnotation(CountLimitParameter.class);
			if (countLimitParameter != null) {
				b = countLimitParameter.value();
			}

			if (b) {
				sb.append("&");
				sb.append(config.getName());
				sb.append("=");
				sb.append(args[i]);
			}
		}
		return sb.toString();
	}
}
