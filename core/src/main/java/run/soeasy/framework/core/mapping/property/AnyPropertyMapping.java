package run.soeasy.framework.core.mapping.property;

import java.util.Collection;
import java.util.List;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.ObjectUtils;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TargetDescriptor;
import run.soeasy.framework.core.convert.value.TypedData;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.KeyValueRegistry;
import run.soeasy.framework.core.exchange.Registry;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

@RequiredArgsConstructor
public class AnyPropertyMapping implements PropertyMapping<PropertyAccessor> {
	@NonNull
	private final Mapping<?, ?> mapping;
	@NonNull
	private final TypeDescriptor typeDescriptor;
	@NonNull
	private final Converter converter;

	@Override
	public Streamable<PropertyAccessor> elements() {
		return mapping.map((kv) -> new ReadyonlyPropertyAccesor(kv));
	}

	@SuppressWarnings("unchecked")
	@Override
	public Streamable<PropertyAccessor> getValues(String key) {
		if (mapping instanceof Registry) {
			PropertyAccessor propertyAccessor = new WritablePropertyAccessor(key,
					(KeyValueRegistry<Object, Object>) mapping);
			return Streamable.singleton(propertyAccessor);
		}
		return PropertyMapping.super.getValues(key);
	}

	@RequiredArgsConstructor
	private class WritablePropertyAccessor implements PropertyAccessor {
		private final String name;
		private final KeyValueRegistry<Object, Object> registry;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public TypeDescriptor getReturnTypeDescriptor() {
			return TypeDescriptor.collection(Collection.class,
					typeDescriptor.upcast(Mapping.class).getResolvableType().getActualTypeArgument(1));
		}

		@Override
		public TypeDescriptor getRequiredTypeDescriptor() {
			return TypeDescriptor.collection(Collection.class,
					typeDescriptor.upcast(Mapping.class).getResolvableType().getActualTypeArgument(1));
		}

		@Override
		public Object get() {
			return elements().filter((e) -> ObjectUtils.equals(name, e.getName())).map((e) -> e.get()).toList();
		}

		@SuppressWarnings("unchecked")
		@Override
		public void set(Object value) {
			Collection<Object> list = (List<Object>) value;
			Object key = converter.convert(name,
					typeDescriptor.upcast(Mapping.class).map((e) -> e.getActualTypeArgument(0)));
			Streamable<KeyValue<Object, Object>> streamable = Streamable.of(list).map((e) -> KeyValue.of(key, e));
			registry.registerAll(streamable);
		}

	}

	@RequiredArgsConstructor
	private class ReadyonlyPropertyAccesor implements PropertyAccessor {
		private final KeyValue<?, ?> keyValue;

		@Override
		public String getName() {
			return converter.convert(keyValue.getKey(), String.class);
		}

		@Override
		public TypeDescriptor getReturnTypeDescriptor() {
			Object value = keyValue.getValue();
			if (value instanceof SourceDescriptor) {
				return ((SourceDescriptor) value).getReturnTypeDescriptor();
			}

			return typeDescriptor.upcast(Mapping.class).map((e) -> e.getActualTypeArgument(1));
		}

		@Override
		public TypeDescriptor getRequiredTypeDescriptor() {
			Object value = keyValue.getValue();
			if (value instanceof TargetDescriptor) {
				return ((TargetDescriptor) value).getRequiredTypeDescriptor();
			}

			return typeDescriptor.upcast(Mapping.class).map((e) -> e.getActualTypeArgument(1)).narrow(value);
		}

		@Override
		public boolean isWriteable() {
			return false;
		}

		@SuppressWarnings({ "rawtypes" })
		@Override
		public Object get() {
			Object value = keyValue.getValue();
			if (value instanceof TypedData) {
				return ((TypedData) value).get();
			}
			return value;
		}

		@Override
		public void set(Object value) {
			throw new UnsupportedOperationException("set");
		}

	}
}
