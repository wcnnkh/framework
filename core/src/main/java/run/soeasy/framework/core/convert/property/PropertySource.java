package run.soeasy.framework.core.convert.property;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.property.collection.MapPropertySource;

@FunctionalInterface
public interface PropertySource extends PropertyTemplate<PropertyAccessor>, Dictionary<PropertyAccessor> {
	public static final PropertySource EMPTY_PROPERTY_TEMPLATE = new EmptyPropertySource();

	public static PropertySource forMap(Map<? extends String, ?> map) {
		return new MapPropertySource(map, TypeDescriptor.map(map.getClass(), String.class, Object.class));
	}

	public static class EmptyPropertySource implements PropertySource, Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public Iterator<PropertyAccessor> iterator() {
			return Collections.emptyIterator();
		}

	}

	public static interface PropertySourceWrapper<W extends PropertySource> extends PropertySource,
			PropertyTemplateWrapper<PropertyAccessor, W>, DictionaryWrapper<PropertyAccessor, W> {

		@Override
		default PropertySource rename(String name) {
			return getSource().rename(name);
		}
	}

	public static class RenamedPropertySource<W extends PropertySource>
			extends RenamedMapping<Object, PropertyAccessor, W> implements PropertySourceWrapper<W> {

		public RenamedPropertySource(@NonNull W source, String name) {
			super(source, name);
		}

		@Override
		public PropertySource rename(String name) {
			return new RenamedPropertySource<>(getSource(), name);
		}
	}

	@Override
	default PropertySource rename(String name) {
		return new RenamedPropertySource<>(this, name);
	}

}
