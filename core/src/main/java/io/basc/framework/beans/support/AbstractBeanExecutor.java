package io.basc.framework.beans.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import io.basc.framework.beans.BeanExecutor;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.mapper.Parameter;
import io.basc.framework.mapper.ParameterDescriptor;
import io.basc.framework.util.Elements;
import io.basc.framework.value.Value;

public abstract class AbstractBeanExecutor implements BeanExecutor {

	@Override
	public final boolean isExecutable() {
		return isExecutable(Elements.empty());
	}

	@Override
	public final Object execute() {
		return execute(Elements.empty());
	}

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

	@Override
	public boolean isExecutableByParameters(Elements<? extends Value> parameters) {
		return organizeParameters(parameters, null);
	}

	@Override
	public Object executeByParameters(Elements<? extends Value> parameters) {
		List<Value> params = new ArrayList<>();
		if (!organizeParameters(parameters, params)) {
			throw new IllegalArgumentException(parameters.toString());
		}
		return execute(Elements.of(params));
	}

}
