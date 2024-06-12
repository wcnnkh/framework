package io.basc.framework.transform.factory.config;

import io.basc.framework.transform.PropertiesTransformer;
import io.basc.framework.transform.factory.PropertiesTransformerFactory;
import io.basc.framework.util.Registration;

public interface PropertiesTransformerRegistry<E extends Throwable> extends PropertiesTransformerFactory<E> {
	<T> Registration registerPropertiesTransformer(Class<T> requiredType,
			PropertiesTransformer<T, ? extends E> propertiesTransformer);
}
