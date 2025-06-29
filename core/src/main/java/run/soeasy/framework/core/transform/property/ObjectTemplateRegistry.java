package run.soeasy.framework.core.transform.property;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.exchange.container.map.DefaultMapContainer;

@Getter
@Setter
public class ObjectTemplateRegistry<E extends Property> extends DefaultMapContainer<Class<?>, PropertyTemplate<E>>
		implements ObjectTemplateFactory<E> {
	private volatile ObjectTemplateFactory<E> objectTemplateFactory;

	public ObjectTemplateRegistry() {
		setReadWriteLock(new ReentrantReadWriteLock());
	}

	@Override
	public PropertyTemplate<E> getObjectTemplate(Class<?> objectClass) {
		PropertyTemplate<E> propertyTemplate = get(objectClass);
		if (propertyTemplate == null && objectTemplateFactory != null
				&& objectTemplateFactory.hasObjectTemplate(objectClass)) {
			Lock lock = writeLock();
			lock.lock();
			try {
				propertyTemplate = get(objectClass);
				if (propertyTemplate == null && objectTemplateFactory != null
						&& objectTemplateFactory.hasObjectTemplate(objectClass)) {
					propertyTemplate = objectTemplateFactory.getObjectTemplate(objectClass);
					put(objectClass, propertyTemplate);
				}
			} finally {
				lock.unlock();
			}
		}
		return propertyTemplate;
	}
}
