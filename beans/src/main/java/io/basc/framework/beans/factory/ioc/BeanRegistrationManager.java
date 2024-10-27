package io.basc.framework.beans.factory.ioc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import io.basc.framework.util.alias.AliasFactory;
import io.basc.framework.util.register.Registration;
import lombok.Data;

@Data
public class BeanRegistrationManager {
	private final Map<String, Registration> registrationMap = new HashMap<>();
	private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	private final AliasFactory aliasFactory;

	public void register(String beanName, Registration registration) {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			Registration old = registrationMap.get(beanName);
			if (old != null) {
				registrationMap.put(beanName, old.and(registration));
			} else {
				for (String alias : aliasFactory.getAliases(beanName)) {
					old = registrationMap.get(alias);
					if (old != null) {
						registrationMap.put(alias, old.and(registration));
						break;
					}
				}

				if (old == null) {
					registrationMap.put(beanName, registration);
				}
			}
		} finally {
			writeLock.unlock();
		}
	}

	public boolean isRegisted(String beanName) {
		Lock readLock = readWriteLock.readLock();
		readLock.lock();
		try {
			if (registrationMap.containsKey(beanName)) {
				return true;
			}

			for (String alias : aliasFactory.getAliases(beanName)) {
				if (registrationMap.containsKey(alias)) {
					return true;
				}
			}
			return false;
		} finally {
			readLock.unlock();
		}
	}

	public void unregister(String beanName) {
		Lock writeLock = readWriteLock.writeLock();
		writeLock.lock();
		try {
			Registration registration = registrationMap.remove(beanName);
			if (registration == null) {
				for (String alias : aliasFactory.getAliases(beanName)) {
					registration = registrationMap.remove(alias);
					if (registration != null) {
						break;
					}
				}
			}

			if (registration != null) {
				registration.unregister();
			}
		} finally {
			writeLock.unlock();
		}
	}
}
