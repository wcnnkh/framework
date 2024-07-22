package io.basc.framework.transform.factory.config;

import io.basc.framework.register.Registration;
import io.basc.framework.transform.PropertiesTransformer;
import io.basc.framework.transform.factory.PropertiesTransformerFactory;

public interface PropertiesTransformerRegistry<E extends Throwable> extends PropertiesTransformerFactory<E> {
	<T> Registration registerPropertiesTransformer(Class<T> requiredType,
			PropertiesTransformer<T, ? extends E> propertiesTransformer);
}
