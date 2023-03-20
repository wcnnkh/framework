package io.basc.framework.dubbo;

import java.util.Collection;

import org.apache.dubbo.config.ReferenceConfig;

public interface DubboReferenceRegistry {
	Collection<ReferenceConfig<?>> getReferences();

	<T> ReferenceConfig<T> register(Class<? extends T> referenceClass);

	void register(ReferenceConfig<?> config);
}
