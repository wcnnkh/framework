package io.basc.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.concurrent.locks.Lock;

import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.stereotype.MappingDescriptorFactory;
import io.basc.framework.util.spi.ServiceMap;
import lombok.NonNull;

public class BeanInfoRegistry extends ServiceMap<CachedBeanInfo>
		implements BeanInfoFactory, MappingDescriptorFactory<BeanFieldDescriptor, BeanMappingDescriptor> {
	private BeanInfoFactory beanInfoFactory;

	protected CachedBeanInfo findBeanInfo(Class<?> beanClass) {
		return getFirst(beanClass);
	}

	public BeanInfoFactory getBeanInfoFactory() {
		return beanInfoFactory;
	}

	public void setBeanInfoFactory(BeanInfoFactory beanInfoFactory) {
		Lock writeLock = getContainer().writeLock();
		try {
			writeLock.lock();
			this.beanInfoFactory = beanInfoFactory;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public CachedBeanInfo getBeanInfo(@NonNull Class<?> beanClass) {
		CachedBeanInfo beanInfo = findBeanInfo(beanClass);
		if (beanInfo == null) {
			Lock lock = getContainer().readLock();
			try {
				lock.lock();
				beanInfo = findBeanInfo(beanClass);
				if (beanInfo == null) {
					Lock writeLock = getContainer().writeLock();
					try {
						writeLock.lock();
						BeanInfo info = loadBeanInfo(beanClass);
						if (info != null) {
							beanInfo = (info instanceof CachedBeanInfo) ? ((CachedBeanInfo) info)
									: new CachedBeanInfo(info);
							set(beanClass, beanInfo);
						}
					} finally {
						writeLock.unlock();
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return beanInfo;
	}

	protected BeanInfo loadBeanInfo(Class<?> beanClass) {
		if (beanInfoFactory == null) {
			return null;
		}

		try {
			return beanInfoFactory.getBeanInfo(beanClass);
		} catch (IntrospectionException ex) {
			throw new FatalBeanException("Failed to obtain BeanInfo for class [" + beanClass.getName() + "]", ex);
		}
	}

	@Override
	public BeanMappingDescriptor getMappingDescriptor(@NonNull TypeDescriptor requiredType) {
		BeanInfo beanInfo = getBeanInfo(requiredType.getType());
		if (beanInfo == null) {
			return null;
		}

		return new BeanMappingDescriptor(requiredType.getType(), beanInfo);
	}
}
