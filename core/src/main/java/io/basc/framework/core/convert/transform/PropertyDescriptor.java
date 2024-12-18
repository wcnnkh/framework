package io.basc.framework.core.convert.transform;

import io.basc.framework.core.annotation.MergedAnnotatedElement;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.ValueDescriptor;
import io.basc.framework.core.type.AnnotatedTypeMetadata;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.alias.Named;
import io.basc.framework.util.select.Selector;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

public interface PropertyDescriptor extends Named, ValueDescriptor {
	@RequiredArgsConstructor
	public static class MergedPropertyDescriptor<E extends PropertyDescriptor>
			implements PropertyDescriptor, AnnotatedTypeMetadata {
		@NonNull
		private final Elements<? extends E> elements;
		private volatile E master;
		private volatile String name;
		private Selector<E> selector = Selector.first();
		private volatile TypeDescriptor typeDescriptor;

		public MergedPropertyDescriptor(@NonNull MergedPropertyDescriptor<E> mergedPropertyDescriptor) {
			this.name = mergedPropertyDescriptor.name;
			this.elements = mergedPropertyDescriptor.elements;
			this.selector = mergedPropertyDescriptor.selector;
			this.master = mergedPropertyDescriptor.master;
			this.typeDescriptor = mergedPropertyDescriptor.typeDescriptor;
		}

		public Elements<? extends E> getElements() {
			return elements;
		}

		public E getMaster() {
			if (master == null) {
				synchronized (this) {
					if (master == null) {
						this.master = selector.select(this.elements);
					}
				}
			}
			return master;
		}

		@Override
		public String getName() {
			if (name == null) {
				synchronized (this) {
					if (name == null) {
						name = getMaster().getName();
					}
				}
			}
			return name;
		}

		public Selector<E> getSelector() {
			return selector;
		}

		@Override
		public TypeDescriptor getTypeDescriptor() {
			if (typeDescriptor == null) {
				synchronized (this) {
					if (typeDescriptor == null) {
						MergedAnnotatedElement mergedAnnotatedElement = new MergedAnnotatedElement(
								elements.map((e) -> e.getTypeDescriptor()));
						TypeDescriptor masterTypeDescriptor = getMaster().getTypeDescriptor();
						this.typeDescriptor = new TypeDescriptor(masterTypeDescriptor.getResolvableType(),
								masterTypeDescriptor.getType(), mergedAnnotatedElement);
					}
				}
			}
			return typeDescriptor;
		}

		@Override
		public MergedPropertyDescriptor<E> rename(String name) {
			MergedPropertyDescriptor<E> mergedPropertyDescriptor = new MergedPropertyDescriptor<>(this.elements);
			mergedPropertyDescriptor.name = name;
			mergedPropertyDescriptor.master = this.master;
			mergedPropertyDescriptor.selector = this.selector;
			mergedPropertyDescriptor.typeDescriptor = this.typeDescriptor;
			return mergedPropertyDescriptor;
		}

		public void setSelector(Selector<E> selector) {
			Assert.requiredArgument(selector != null, "selector");
			synchronized (this) {
				Assert.isTrue(master != null, "Master has already been selected, cannot set selector again");
			}
			this.selector = selector;
		}

		@Override
		public MergedAnnotations getAnnotations() {
			return MergedAnnotations.from(elements);
		}
	}

	@FunctionalInterface
	public static interface PropertyDescriptorWrapper<W extends PropertyDescriptor>
			extends PropertyDescriptor, NamedWrapper<W>, ValueDescriptorWrapper<W> {

		@Override
		default PropertyDescriptor rename(String name) {
			return getSource().rename(name);
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

	@Override
	default PropertyDescriptor rename(String name) {
		return new RenamedPropertyDescriptor<>(name, this);
	}
}
