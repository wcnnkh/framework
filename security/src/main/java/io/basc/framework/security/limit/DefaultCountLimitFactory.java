package io.basc.framework.security.limit;

import io.basc.framework.context.annotation.ConditionalOnParameters;
import io.basc.framework.core.Ordered;
import io.basc.framework.core.execution.ParameterDescriptor;
import io.basc.framework.core.execution.param.ParameterUtils;
import io.basc.framework.security.limit.annotation.CountLimitParameter;
import io.basc.framework.security.limit.annotation.CountLimitSecurity;
import io.basc.framework.util.Elements;
import io.basc.framework.util.Indexed;
import io.basc.framework.util.reflect.MethodInvoker;

@ConditionalOnParameters(order = Ordered.LOWEST_PRECEDENCE)
public class DefaultCountLimitFactory implements CountLimitFactory {

	public String getKey(CountLimitSecurity countLimitSecurity, MethodInvoker invoker, Object[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append("count-limit:");
		sb.append(invoker.getMethod().getName());
		Elements<ParameterDescriptor> parameterConfigs = ParameterUtils.getParameters(invoker.getMethod());
		for(Indexed<ParameterDescriptor> index : parameterConfigs.index()) {
			ParameterDescriptor config = index.getElement();
			boolean b = countLimitSecurity.useAllParameters();
			CountLimitParameter countLimitParameter = config.getTypeDescriptor().getAnnotation(CountLimitParameter.class);
			if (countLimitParameter != null) {
				b = countLimitParameter.value();
			}

			if (b) {
				sb.append("&");
				sb.append(config.getName());
				sb.append("=");
				sb.append(args[(int) index.getIndex()]);
			}
		}
		return sb.toString();
	}
}
