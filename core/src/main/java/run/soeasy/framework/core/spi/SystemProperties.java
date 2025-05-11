package run.soeasy.framework.core.spi;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.convert.AccessibleDescriptor;
import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.convert.TypedValue;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.transform.indexed.IndexedAccessor;
import run.soeasy.framework.core.transform.indexed.PropertyMapping;

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
	public Iterator<IndexedAccessor> iterator() {
		return keys().map((key) -> get(key)).iterator();
	}

	@Override
	public IndexedAccessor get(Object key) {
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
	private static class SystemProperty implements IndexedAccessor {
		@NonNull
		private final String index;
		@NonNull
		private ConversionService conversionService = SystemConversionService.getInstance();

		@Override
		public void set(Object source) throws UnsupportedOperationException {
			String value = (String) conversionService.apply(TypedValue.of(source),
					AccessibleDescriptor.forTypeDescriptor(STRING_TYPE_DESCRIPTOR));
			System.setProperty(index, value);
		}

		@Override
		public Object get() {
			String value = System.getProperty(index);
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
