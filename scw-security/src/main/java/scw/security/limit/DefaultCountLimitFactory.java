package scw.security.limit;

import scw.core.parameter.ParameterDescriptor;
import scw.core.parameter.ParameterUtils;
import scw.core.reflect.MethodInvoker;
import scw.security.limit.annotation.CountLimitParameter;
import scw.security.limit.annotation.CountLimitSecurity;

public class DefaultCountLimitFactory implements CountLimitFactory {

	public String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("count-limit:");
		sb.append(invoker.getMethod().getName());
		ParameterDescriptor[] parameterConfigs = ParameterUtils.getParameterDescriptors(invoker.getMethod());
		for (int i = 0; i < parameterConfigs.length; i++) {
			ParameterDescriptor config = parameterConfigs[i];
			boolean b = countLimitSecurity.useAllParameters();
			CountLimitParameter countLimitParameter = config.getAnnotatedElement()
					.getAnnotation(CountLimitParameter.class);
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
