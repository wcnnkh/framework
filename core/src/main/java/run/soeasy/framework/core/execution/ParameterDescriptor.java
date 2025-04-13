package run.soeasy.framework.core.execution;

import java.lang.reflect.Executable;
import java.util.Iterator;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.transform.stereotype.PropertyDescriptor;

public interface ParameterDescriptor extends PropertyDescriptor {
	@Getter
	public static class ExecutableParameterDescriptor<W extends Executable> implements ParameterDescriptor {
		private final int index;
		private volatile String[] names;
		@NonNull
		private final ParameterNameDiscoverer parameterNameDiscoverer;

		@NonNull
		private final W source;

		private volatile TypeDescriptor[] typeDescriptors;

		public ExecutableParameterDescriptor(int index, @NonNull W source,
				ParameterNameDiscoverer parameterNameDiscoverer) {
			Assert.isTrue(index >= 0 && index < source.getParameterCount(),
					"To be greater than 0 and less than the number of parameters");
			this.index = index;
			this.source = source;
			this.parameterNameDiscoverer = parameterNameDiscoverer;
		}

		@Override
		public String getName() {
			if (names == null) {
				synchronized (this) {
					if (names == null) {
						names = parameterNameDiscoverer.getParameterNames(source);
					}
				}
			}
			return names == null ? ("arg" + 1) : names[index];
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			if (typeDescriptors == null) {
				synchronized (this) {
					if (typeDescriptors == null) {
						typeDescriptors = new TypeDescriptor[source.getParameterCount()];
					}
				}
			}

			TypeDescriptor typeDescriptor = typeDescriptors[index];
			if (typeDescriptor == null) {
				synchronized (this) {
					typeDescriptor = typeDescriptors[index];
					if (typeDescriptor == null) {
						typeDescriptors[index] = typeDescriptor = TypeDescriptor.forExecutableParameter(source, index);
					}
				}
			}
			return typeDescriptor;
		}

		public ExecutableParameterDescriptor<W> reindex(int index) {
			if (index == this.index) {
				return this;
			}

			ExecutableParameterDescriptor<W> descriptor = new ExecutableParameterDescriptor<>(index, source,
					parameterNameDiscoverer);
			descriptor.names = this.names;
			descriptor.typeDescriptors = this.typeDescriptors;
			return descriptor;
		}
	}

	@RequiredArgsConstructor
	public static class ExecutableParameterDescriptorIterable<W extends Executable>
			implements Iterable<ExecutableParameterDescriptor<W>> {
		@RequiredArgsConstructor
		private static class ExecutableParameterDescriptorIterator<W extends Executable>
				implements Iterator<ExecutableParameterDescriptor<W>> {
			private int index;
			@NonNull
			private final W source;
			@NonNull
			private final ParameterNameDiscoverer parameterNameDiscoverer;

			private volatile ExecutableParameterDescriptor<W> parameterDescriptor;

			@Override
			public boolean hasNext() {
				return index < source.getParameterCount();
			}

			@Override
			public ExecutableParameterDescriptor<W> next() {
				if (parameterDescriptor == null) {
					synchronized (this) {
						if (parameterDescriptor == null) {
							parameterDescriptor = new ExecutableParameterDescriptor<>(index, source,
									parameterNameDiscoverer);
						}
					}
				}

				return parameterDescriptor.reindex(index++);
			}
		}

		@NonNull
		private final W source;
		@NonNull
		private final ParameterNameDiscoverer parameterNameDiscoverer;

		@Override
		public Iterator<ExecutableParameterDescriptor<W>> iterator() {
			return new ExecutableParameterDescriptorIterator<>(source, parameterNameDiscoverer);
		}
	}

	@FunctionalInterface
	public static interface ParameterDescriptorWrapper<W extends ParameterDescriptor>
			extends ParameterDescriptor, PropertyDescriptorWrapper<W> {
		@Override
		default int getIndex() {
			return getSource().getIndex();
		}

		@Override
		default ParameterDescriptor rename(String name) {
			return getSource().rename(name);
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
	public static class SharedParameterDescriptor<W extends PropertyDescriptor> extends SharedPropertyDescriptor<W>
			implements ParameterDescriptor, PropertyDescriptorWrapper<W> {
		private static final long serialVersionUID = 1L;
		private int index = -1;

		public SharedParameterDescriptor(@NonNull W source) {
			super(source);
		}

		@Override
		public ParameterDescriptor rename(String name) {
			return ParameterDescriptor.super.rename(name);
		}
	}

	public static final ParameterDescriptor[] EMPTY_ARRAY = new ParameterDescriptor[0];

	public static Elements<ParameterDescriptor> forExecutable(Executable executable) {
		return Elements.of(forExecutable(executable, SystemParameterNameDiscoverer.getInstance()));
	}

	public static <W extends Executable> ExecutableParameterDescriptorIterable<W> forExecutable(@NonNull W executable,
			@NonNull ParameterNameDiscoverer parameterNameDiscoverer) {
		return new ExecutableParameterDescriptorIterable<>(executable, parameterNameDiscoverer);
	}

	public static ParameterDescriptor of(int index, @NonNull PropertyDescriptor propertyDescriptor) {
		if (propertyDescriptor instanceof ParameterDescriptor) {
			ParameterDescriptor parameterDescriptor = (ParameterDescriptor) propertyDescriptor;
			if (parameterDescriptor.getIndex() == index) {
				return parameterDescriptor;
			}

			return parameterDescriptor.reindex(index);
		}

		SharedParameterDescriptor<PropertyDescriptor> shared = new SharedParameterDescriptor<>(propertyDescriptor);
		shared.setIndex(index);
		return shared;
	}

	int getIndex();

	default ParameterDescriptor rename(String name) {
		return new RenamedParameterDescriptor<>(name, this);
	}

	default ParameterDescriptor reindex(int index) {
		SharedParameterDescriptor<PropertyDescriptor> shared = new SharedParameterDescriptor<PropertyDescriptor>(this);
		shared.setIndex(index);
		return shared;
	}
}
