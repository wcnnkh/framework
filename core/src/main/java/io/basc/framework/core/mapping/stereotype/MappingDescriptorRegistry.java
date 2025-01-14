package io.basc.framework.core.mapping.stereotype;

import java.util.concurrent.locks.Lock;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class MappingDescriptorRegistry<D extends FieldDescriptor, T extends MappingDescriptor<? extends D>>
		extends ServiceMap<T> implements MappingDescriptorFactory<D, T> {
	private MappingDescriptorFactory<D, ? extends T> mappingDescriptorFactory;

	public boolean containsTemplate(Class<?> requiredType) {
		return !search(requiredType).isEmpty();
	}

	protected T findMappingDescriptor(TypeDescriptor requiredType) {
		return search(requiredType.getType()).first();
	}

	@Override
	public T getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
		T mappingDescriptor = findMappingDescriptor(requiredType);
		if (mappingDescriptor == null) {
			Lock lock = getContainer().readLock();
			try {
				lock.lock();
				mappingDescriptor = findMappingDescriptor(requiredType);
				if (mappingDescriptor == null) {
					Lock writeLock = getContainer().writeLock();
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

	public MappingDescriptorFactory<D, ? extends T> getMappingDescriptorFactory() {
		return mappingDescriptorFactory;
	}

	protected T loadMappingDescriptor(TypeDescriptor requiredType) {
		return mappingDescriptorFactory == null ? null : mappingDescriptorFactory.getMappingDescriptor(requiredType);
	}

	public void setMappingDescriptorFactory(MappingDescriptorFactory<D, T> mappingDescriptorFactory) {
		Lock lock = getContainer().writeLock();
		try {
			lock.lock();
			this.mappingDescriptorFactory = mappingDescriptorFactory;
		} finally {
			lock.unlock();
		}
	}
}
