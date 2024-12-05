package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface ParameterDescriptor extends PropertyDescriptor {
	public static interface ParameterDescriptorWrapper<W extends ParameterDescriptor>
			extends ParameterDescriptor, PropertyDescriptorWrapper<W> {
		@Override
		default int getIndex() {
			return getSource().getIndex();
		}
	}

	public static class RenamedParameterDescriptor<W extends ParameterDescriptor> extends RenamedPropertyDescriptor<W>
			implements ParameterDescriptorWrapper<W> {

		public RenamedParameterDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public ParameterDescriptor rename(String name) {
			return new RenamedParameterDescriptor<>(name, getSource());
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class SimpleParameterDescriptor extends SimplePropertyDescriptor implements ParameterDescriptor {
		private static final long serialVersionUID = 1L;
		private int index;

		public SimpleParameterDescriptor(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name, typeDescriptor);
			this.index = index;
		}
	}

	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	public static ParameterDescriptor of(int index, @NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
		return new SimpleParameterDescriptor(index, name, typeDescriptor);
	}

	int getIndex();

	default ParameterDescriptor rename(String name) {
		return new RenamedParameterDescriptor<>(name, this);
	}
}
