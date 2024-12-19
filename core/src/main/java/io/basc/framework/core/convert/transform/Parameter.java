package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface Parameter extends ParameterDescriptor, Property {

	@FunctionalInterface
	public static interface ParameterWrapper<W extends Parameter>
			extends Parameter, PropertyWrapper<W>, ParameterDescriptorWrapper<W> {
	}

	@Data
	@ToString(callSuper = true)
	@EqualsAndHashCode(callSuper = true)
	public static class SimpleParmeter extends SimpleProperty implements Parameter, PropertyWrapper<Property> {
		private static final long serialVersionUID = 1L;
		private int index;
		private Property source;

		public SimpleParmeter(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name, typeDescriptor);
			this.index = index;
		}

		@Override
		public ParameterDescriptor rename(String name) {
			return Parameter.super.rename(name);
		}
	}

	@Data
	public static class PropertyParameter implements Parameter {
		private int index;
		@NonNull
		private Property property;

		public PropertyParameter(int index, @NonNull Property property) {
			this.index = index;
			this.property = property;
		}

		@Override
		public String getName() {
			return property.getName();
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			return property.getTypeDescriptor();
		}

		@Override
		public void set(Object source) throws UnsupportedOperationException {
			property.set(source);
		}

		@Override
		public Object get() {
			return property.get();
		}

	}

	public static Parameter of(int index, @NonNull Property property) {
		return new PropertyParameter(index, property);
	}

	public static Parameter of(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
		return new SimpleParmeter(index, name, typeDescriptor);
	}
}
