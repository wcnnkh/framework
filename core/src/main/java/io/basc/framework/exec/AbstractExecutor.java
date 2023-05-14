package io.basc.framework.exec;

import java.util.Iterator;
import java.util.List;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public abstract class AbstractExecutor extends AbstractExecutable implements Executor {

	@Override
	public boolean isExecutable(Elements<? extends TypeDescriptor> types) {
		return getParameterDescriptors().equals(types, (param, type) -> type.isAssignableTo(param.getTypeDescriptor()));
	}

	public boolean organizeParameters(Elements<? extends Value> parameters, List<Value> results) {
		List<? extends Value> list = parameters.toList();
		Iterator<? extends ParameterDescriptor> parameterDescriptorIterator = getParameterDescriptors().iterator();
		while (parameterDescriptorIterator.hasNext() && !list.isEmpty()) {
			ParameterDescriptor parameterDescriptor = parameterDescriptorIterator.next();
			boolean find = false;
			// 优先使用名称匹配
			Iterator<? extends Value> iterator = list.iterator();
			while (iterator.hasNext()) {
				Value value = iterator.next();
				if (value instanceof Parameter) {
					Parameter parameter = (Parameter) value;
					if (parameterDescriptor.test(parameter)) {
						if (results != null) {
							results.add(parameter);
						}
						iterator.remove();
						break;
					}
				}
			}

			if (!find) {
				// 如果没找到，使用类型匹配
				iterator = list.iterator();
				while (iterator.hasNext()) {
					Value value = iterator.next();
					if (value.getTypeDescriptor().isAssignableTo(parameterDescriptor.getTypeDescriptor())) {
						if (results != null) {
							results.add(value);
						}
						iterator.remove();
						break;
					}
				}
			}

			if (!find) {
				if (parameterDescriptor.isNullable()) {
					if (results != null) {
						Parameter nullParameter = new Parameter(parameterDescriptor.getName(), null,
								parameterDescriptor.getTypeDescriptor());
						results.add(nullParameter);
					}
				} else {
					return false;
				}
			}
		}
		return !parameterDescriptorIterator.hasNext() && list.isEmpty();
	}
}
