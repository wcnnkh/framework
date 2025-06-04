package run.soeasy.framework.core.transform.object;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.core.exchange.container.map.DefaultMapContainer;
import run.soeasy.framework.core.type.ClassMembersLoader;

@Getter
@Setter
public class ClassMemberTemplateRegistry<E extends Property>
		extends DefaultMapContainer<Class<?>, ClassMembersLoader<E>> implements ClassMemberTemplateFactory<E> {
	private volatile ClassMemberTemplateFactory<E> classPropertyTemplateFactory;

	public ClassMemberTemplateRegistry() {
		setReadWriteLock(new ReentrantReadWriteLock());
	}

	@Override
	public ClassMembersLoader<E> getClassPropertyTemplate(Class<?> requiredClass) {
		ClassMembersLoader<E> classMembersLoader = get(requiredClass);
		if (classMembersLoader == null && classPropertyTemplateFactory != null
				&& classPropertyTemplateFactory.hasClassPropertyTemplate(requiredClass)) {
			Lock lock = writeLock();
			lock.lock();
			try {
				classMembersLoader = get(requiredClass);
				if (classMembersLoader == null && classPropertyTemplateFactory != null
						&& classPropertyTemplateFactory.hasClassPropertyTemplate(requiredClass)) {
					classMembersLoader = classPropertyTemplateFactory.getClassPropertyTemplate(requiredClass);
					if (classMembersLoader != null) {
						put(requiredClass, classMembersLoader);
					}
				}
			} finally {
				lock.unlock();
			}
		}
		return classMembersLoader;
	}

}
