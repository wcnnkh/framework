package run.soeasy.framework.core.spi;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.transform.property.PropertyAccessor;
import run.soeasy.framework.core.transform.property.PropertyMapping;

public final class SystemProperties implements PropertyMapping {
	private static final TypeDescriptor STRING_TYPE_DESCRIPTOR = TypeDescriptor.valueOf(String.class);
	private static volatile SystemProperties instance;

	public static SystemProperties getInstance() {
		if (instance == null) {
			synchronized (SystemProperties.class) {
				if (instance == null) {
					instance = new SystemProperties();
				}
			}
		}
		return instance;
	}

	private SystemProperties() {
	}

	@Override
	public Iterator<PropertyAccessor> iterator() {
		return keys().map((key) -> get(key)).iterator();
	}

	@Override
	public PropertyAccessor get(Object key) {
		String keyToUse = String.valueOf(key);
		return new SystemProperty(keyToUse);
	}

	@Override
	public Elements<Object> keys() {
		Elements<Object> systemKeys = Elements.of(System.getProperties().stringPropertyNames());
		Elements<Object> envKeys = Elements.of(System.getenv().keySet());
		return systemKeys.concat(envKeys).distinct();
	}

	@RequiredArgsConstructor
	@AllArgsConstructor
	@Getter
	@Setter
	private static class SystemProperty implements PropertyAccessor {
		@NonNull
		private final String name;
		@NonNull
		private ConversionService conversionService = SystemConversionService.getInstance();

		@Override
		public void set(Object source) throws UnsupportedOperationException {
			String value = (String) conversionService.convert(source, STRING_TYPE_DESCRIPTOR);
			System.setProperty(name, value);
		}

		@Override
		public Object get() {
			String value = System.getProperty(name);
			if (value == null) {
				value = System.getenv(value);
			}
			return value;
		}

		@Override
		public boolean isReadable() {
			return get() != null;
		}

		@Override
		public TypeDescriptor getReturnTypeDescriptor() {
			return STRING_TYPE_DESCRIPTOR;
		}

		@Override
		public TypeDescriptor getRequiredTypeDescriptor() {
			return STRING_TYPE_DESCRIPTOR;
		}
	}
}
