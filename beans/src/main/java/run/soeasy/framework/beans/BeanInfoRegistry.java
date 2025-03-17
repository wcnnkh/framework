package run.soeasy.framework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.util.concurrent.locks.Lock;

import lombok.NonNull;
import run.soeasy.framework.util.spi.ServiceMap;

public class BeanInfoRegistry extends ServiceMap<BeanInfo> implements BeanInfoProvider {
	private BeanInfoFactory beanInfoFactory;

	protected BeanInfo findBeanInfo(Class<?> beanClass) {
		return getFirst(beanClass);
	}

	public BeanInfoFactory getBeanInfoFactory() {
		return beanInfoFactory;
	}

	public void setBeanInfoFactory(BeanInfoFactory beanInfoFactory) {
		Lock writeLock = writeLock();
		try {
			writeLock.lock();
			this.beanInfoFactory = beanInfoFactory;
		} finally {
			writeLock.unlock();
		}
	}

	@Override
	public BeanInfo getBeanInfo(@NonNull Class<?> beanClass) {
		BeanInfo beanInfo = findBeanInfo(beanClass);
		if (beanInfo == null) {
			Lock lock = readLock();
			try {
				lock.lock();
				beanInfo = findBeanInfo(beanClass);
				if (beanInfo == null) {
					Lock writeLock = writeLock();
					try {
						writeLock.lock();
						BeanInfo info = loadBeanInfo(beanClass);
						if (info != null) {
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

}
