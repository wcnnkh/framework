package io.basc.framework.core.convert.transform;

import java.util.Arrays;
import java.util.List;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.Elements;
import lombok.Data;
import lombok.NonNull;

/**
 * 多个参数的定义
 * 
 * @author shuchaowen
 *
 */
public interface Parameters extends ParameterMapping<Parameter> {

	@Data
	public static class SimpleParameters implements Parameters {
		@NonNull
		private List<Parameter> parameterList;

		@Override
		public Parameter get(int index) {
			return parameterList == null ? null : parameterList.get(index);
		}

		@Override
		public Elements<Parameter> getElements() {
			if (parameterList == null) {
				return Elements.empty();
			}

			return Elements.of(parameterList);
		}

		@Override
		public int size() {
			return parameterList == null ? 0 : parameterList.size();
		}
	}

	public static Parameters forArgs(@NonNull Object... args) {
		Parameter[] parameters = new Parameter[args.length];
		for (int i = 0; i < parameters.length; i++) {
			Object value = args[i];
			Parameter parameter = Parameter.of(i, "arg" + i, TypeDescriptor.forObject(value));
			parameter.set(value);
			parameters[i] = parameter;
		}
		return of(parameters);
	}

	public static Parameters forProperties(@NonNull Property... properties) {
		Parameter[] parameters = new Parameter[properties.length];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = Parameter.of(i, properties[i]);
		}
		return of(parameters);
	}

	public static Parameters of(@NonNull Parameter... parameters) {
		return new SimpleParameters(Arrays.asList(parameters));
	}
}
