package run.soeasy.framework.core.transform.mapping;

import java.util.function.Predicate;

import lombok.Data;
import lombok.NonNull;
import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.mapping.PropertyAccessor;
import run.soeasy.framework.core.convert.mapping.PropertyDescriptor;
import run.soeasy.framework.core.convert.value.ValueAccessor;

public interface ParameterAccessor extends ParameterDescriptor, PropertyAccessor, Predicate<ParameterDescriptor> {

	@FunctionalInterface
	public static interface ParameterWrapper<W extends ParameterAccessor>
			extends ParameterAccessor, PropertyWrapper<W>, ParameterDescriptorWrapper<W> {
		@Override
		default ParameterAccessor rename(String name) {
			return ParameterAccessor.super.rename(name);
		}
	}

	public static class StandardParmeter<W extends ParameterDescriptor> extends StandardProperty<W>
			implements ParameterAccessor, ParameterDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;

		public StandardParmeter(@NonNull W source) {
			super(source);
		}

		@Override
		public ParameterAccessor rename(String name) {
			return ParameterAccessor.super.rename(name);
		}
	}

	@Data
	public static class PropertyParameter<W extends PropertyAccessor> implements ParameterAccessor, PropertyWrapper<W> {
		private final int index;
		@NonNull
		private final W source;

		@Override
		public ParameterAccessor rename(String name) {
			return ParameterAccessor.super.rename(name);
		}
	}

	public static ParameterAccessor of(int index, @NonNull PropertyAccessor property) {
		return new PropertyParameter<>(index, property);
	}

	public static ParameterAccessor of(int index, @NonNull PropertyDescriptor propertyDescriptor) {
		return of(ParameterDescriptor.of(index, propertyDescriptor));
	}

	public static ParameterAccessor of(int index, String name, @NonNull ValueAccessor value) {
		PropertyAccessor property = PropertyAccessor.of(name, value);
		return of(index, property);
	}

	public static ParameterAccessor of(@NonNull ParameterDescriptor parameterDescriptor) {
		if (parameterDescriptor instanceof ParameterAccessor) {
			return (ParameterAccessor) parameterDescriptor;
		}
		return new StandardParmeter<>(parameterDescriptor);
	}

	/**
	 * 根据模板重构一个完整的参数
	 * 
	 * @param template
	 * @return
	 */
	default ParameterAccessor reconstruct(ParameterDescriptor template) {
		ParameterAccessor parameter = of(template);
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
	default ParameterAccessor rename(String name) {
		// TODO Auto-generated method stub
		return null;
	}
}
