package run.soeasy.framework.core.param;

import java.util.function.Predicate;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.transform.stereotype.Property;
import run.soeasy.framework.core.convert.transform.stereotype.PropertyDescriptor;
import run.soeasy.framework.util.StringUtils;

public interface Parameter extends ParameterDescriptor, Property, Predicate<ParameterDescriptor> {

	@FunctionalInterface
	public static interface ParameterWrapper<W extends Parameter>
			extends Parameter, PropertyWrapper<W>, ParameterDescriptorWrapper<W> {
		@Override
		default Parameter rename(String name) {
			return Parameter.super.rename(name);
		}
	}

	public static class StandardParmeter<W extends ParameterDescriptor> extends StandardProperty<W>
			implements Parameter, ParameterDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public StandardParmeter(@NonNull W source) {
			super(source);
		}

		@Override
		public Parameter rename(String name) {
			return Parameter.super.rename(name);
		}
	}

	@Data
	public static class PropertyParameter<W extends Property> implements Parameter, PropertyWrapper<W> {
		private final int index;
		@NonNull
		private final W source;

		@Override
		public Parameter rename(String name) {
			return Parameter.super.rename(name);
		}
	}

	public static Parameter of(int index, @NonNull Property property) {
		return new PropertyParameter<>(index, property);
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
		return new StandardParmeter<>(parameterDescriptor);
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

	@Override
	default Parameter rename(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
