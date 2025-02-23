package io.basc.framework.core.execution;

import java.util.function.Predicate;

import io.basc.framework.core.convert.Source;
import io.basc.framework.core.convert.transform.stereotype.Property;
import io.basc.framework.core.convert.transform.stereotype.PropertyDescriptor;
import io.basc.framework.util.StringUtils;
import lombok.Data;
import lombok.NonNull;

public interface Parameter extends ParameterDescriptor, Property, Predicate<ParameterDescriptor> {

	@FunctionalInterface
	public static interface ParameterWrapper<W extends Parameter>
			extends Parameter, PropertyWrapper<W>, ParameterDescriptorWrapper<W> {
	}

	public static class SharedParmeter<W extends ParameterDescriptor> extends SharedProperty<W>
			implements Parameter, ParameterDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public SharedParmeter(@NonNull W source) {
			super(source);
		}
	}

	@Data
	public static class StandardParameter<W extends Property> implements Parameter, PropertyWrapper<W> {
		private final int index;
		@NonNull
		private final W source;

		@Override
		public ParameterDescriptor rename(String name) {
			return Parameter.super.rename(name);
		}
	}

	public static Parameter of(int index, @NonNull Property property) {
		return new StandardParameter<>(index, property);
	}

	public static Parameter of(int index, @NonNull PropertyDescriptor propertyDescriptor) {
		return of(ParameterDescriptor.of(index, propertyDescriptor));
	}

	public static Parameter of(int index, String name, @NonNull Source value) {
		Property property = Property.of(name, value);
		return of(index, property);
	}

	public static Parameter of(@NonNull ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor instanceof Parameter) {
			return (Parameter) parameterDescriptor;
		}
		return new SharedParmeter<>(parameterDescriptor);
	}

	/**
	 * 根据模板重构一个完整的参数
	 * 
	 * @param template
	 * @return
	 */
	default Parameter reconstruct(ParameterDescriptor template) {
		Parameter parameter = of(template);
		parameter.set(get());
		return parameter;
	}

	@Override
	default boolean test(ParameterDescriptor template) {
		if (template.getIndex() == getIndex() || StringUtils.equals(getName(), template.getName())) {
			if (getTypeDescriptor().isAssignableTo(template.getRequiredTypeDescriptor())) {
				if (template.isRequired() && isReadable() && get() != null) {
					return true;
				}
			}
		}
		return false;
	}
}
