package run.soeasy.framework.core.convert.mapping;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.mapping.collection.MapPropertyTemplate;

@FunctionalInterface
public interface PropertyTemplate extends PropertyDescriptors<PropertyAccessor>, Dictionary<PropertyAccessor> {
	public static final PropertyTemplate EMPTY_PROPERTY_TEMPLATE = new EmptyPropertySource();

	public static PropertyTemplate forMap(Map<? extends String, ?> map) {
		return new MapPropertyTemplate(map, TypeDescriptor.map(map.getClass(), String.class, Object.class));
	}

	public static class EmptyPropertySource implements PropertyTemplate, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Iterator<PropertyAccessor> iterator() {
			return Collections.emptyIterator();
		}

	}

	public static interface PropertyTemplateWrapper<W extends PropertyTemplate> extends PropertyTemplate,
			PropertyDescriptorsWrapper<PropertyAccessor, W>, DictionaryWrapper<PropertyAccessor, W> {

		@Override
		default PropertyTemplate rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedPropertyTemplate<W extends PropertyTemplate>
			extends RenamedTemplate<Object, PropertyAccessor, W> implements PropertyTemplateWrapper<W> {

		public RenamedPropertyTemplate(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public PropertyTemplate rename(String name) {
			return new RenamedPropertyTemplate<>(getSource(), name);
		}
	}

	@Override
	default PropertyTemplate rename(String name) {
		return new RenamedPropertyTemplate<>(this, name);
	}

}
