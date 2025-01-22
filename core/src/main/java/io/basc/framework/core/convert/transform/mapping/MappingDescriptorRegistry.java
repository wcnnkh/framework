package io.basc.framework.core.convert.transform.mapping;

import java.util.concurrent.locks.Lock;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ServiceMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class MappingDescriptorRegistry<SD extends FieldDescriptor, SM extends MappingDescriptor<? extends SD>>
		extends ServiceMap<SM> implements MappingTemplateProvider<SD, SM> {
	private MappingDescriptorFactory<SD, ? extends SM> mappingDescriptorFactory;

	protected SM findMappingDescriptor(TypeDescriptor requiredType) {
		return search(requiredType.getType()).first();
	}

	@Override
	public SM getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
		SM mappingDescriptor = findMappingDescriptor(requiredType);
		if (mappingDescriptor == null) {
			Lock lock = readLock();
			try {
				lock.lock();
				mappingDescriptor = findMappingDescriptor(requiredType);
				if (mappingDescriptor == null) {
					Lock writeLock = writeLock();
					try {
						writeLock.lock();
						mappingDescriptor = loadMappingDescriptor(requiredType);
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

	protected SM loadMappingDescriptor(TypeDescriptor requiredType) {
		return mappingDescriptorFactory == null ? null : mappingDescriptorFactory.getMappingDescriptor(requiredType);
	}
}