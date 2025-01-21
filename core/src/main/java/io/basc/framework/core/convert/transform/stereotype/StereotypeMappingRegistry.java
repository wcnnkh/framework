package io.basc.framework.core.convert.transform.stereotype;

import java.util.concurrent.locks.Lock;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ServiceMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class StereotypeMappingRegistry<SD extends StereotypeDescriptor, SM extends StereotypeMapping<? extends SD>>
		extends ServiceMap<SM> implements StereotypeTemplateProvider<SD, SM> {
	private StereotypeMappingFactory<SD, ? extends SM> stereotypeMappingFactory;

	protected SM findStereotypeMapping(TypeDescriptor requiredType) {
		return search(requiredType.getType()).first();
	}

	@Override
	public SM getStereotypeMapping(@NonNull TypeDescriptor requiredType) {
		SM mappingDescriptor = findStereotypeMapping(requiredType);
		if (mappingDescriptor == null) {
			Lock lock = readLock();
			try {
				lock.lock();
				mappingDescriptor = findStereotypeMapping(requiredType);
				if (mappingDescriptor == null) {
					Lock writeLock = writeLock();
					try {
						writeLock.lock();
						mappingDescriptor = loadStereotypeMapping(requiredType);
						if (mappingDescriptor != null) {
							set(requiredType.getType(), mappingDescriptor);
						}
					} finally {
						writeLock.unlock();
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return mappingDescriptor;
	}

	protected SM loadStereotypeMapping(TypeDescriptor requiredType) {
		return stereotypeMappingFactory == null ? null : stereotypeMappingFactory.getStereotypeMapping(requiredType);
	}
}
