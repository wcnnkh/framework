package io.basc.framework.core.convert.transform;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.alias.Named;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

public interface PropertyDescriptor extends Named {
	public static interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
			extends PropertyDescriptor, NamedWrapper<W> {
		@Override
		default TypeDescriptor getTypeDescriptor() {
			return getSource().getTypeDescriptor();
		}
	}

	public static class RenamedPropertyDescriptor<W extends PropertyDescriptor> extends Renamed<W>
			implements PropertyDescriptorWrapper<W> {

		public RenamedPropertyDescriptor(@NonNull String name, @NonNull W source) {
			super(name, source);
		}

		@Override
		public PropertyDescriptor rename(String name) {
			return new RenamedPropertyDescriptor<>(name, getSource());
		}
	}

	@Data
	@EqualsAndHashCode(callSuper = true)
	@ToString(callSuper = true)
	public static class SimplePropertyDescriptor extends SimpleNamed implements PropertyDescriptor {
		private static final long serialVersionUID = 1L;
		@NonNull
		private TypeDescriptor typeDescriptor;

		public SimplePropertyDescriptor(@NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
			super(name);
			this.typeDescriptor = typeDescriptor;
		}
	}
	
	public static PropertyDescriptor of(@NonNull String name, @NonNull TypeDescriptor typeDescriptor) {
		return new SimplePropertyDescriptor(name, typeDescriptor);
	}

	TypeDescriptor getTypeDescriptor();

	@Override
	default PropertyDescriptor rename(String name) {
		return new RenamedPropertyDescriptor<>(name, this);
	}
}
