package run.soeasy.framework.core.convert.transform.stereotype;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.annotation.MergedAnnotatedElement;
import run.soeasy.framework.core.annotation.MergedAnnotations;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.type.AnnotatedTypeMetadata;
import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.alias.Named;
import run.soeasy.framework.util.collections.Elements;
import run.soeasy.framework.util.function.Selector;

public interface PropertyDescriptor extends Named, AccessDescriptor {
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
						this.master = selector.apply(this.elements);
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
			extends PropertyDescriptor, NamedWrapper<W>, AccessDescriptorWrapper<W> {

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
	public static class SharedPropertyDescriptor<W extends AccessDescriptor>
			implements PropertyDescriptor, AccessDescriptorWrapper<W>, Serializable {
		private static final long serialVersionUID = 1L;
		private String name;
		@NonNull
		private final W source;

		public SharedPropertyDescriptor(@NonNull W source) {
			this.source = source;
		}
	}

	public static PropertyDescriptor of(String name, @NonNull AccessDescriptor accessDescriptor) {
		if (accessDescriptor instanceof PropertyDescriptor) {
			PropertyDescriptor propertyDescriptor = (PropertyDescriptor) accessDescriptor;
			if (StringUtils.equals(name, propertyDescriptor.getName())) {
				return propertyDescriptor;
			}
			return propertyDescriptor.rename(name);
		}

		SharedPropertyDescriptor<AccessDescriptor> shared = new SharedPropertyDescriptor<>(accessDescriptor);
		shared.setName(name);
		return shared;
	}

	@Override
	default PropertyDescriptor rename(String name) {
		return new RenamedPropertyDescriptor<>(name, this);
	}
}
