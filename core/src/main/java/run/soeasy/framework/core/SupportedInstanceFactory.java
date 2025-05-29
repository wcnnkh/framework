package run.soeasy.framework.core;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.CollectionFactory;

/**
 * 已支持的实例工厂
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
public enum SupportedInstanceFactory implements InstanceFactory {
	COLLECTION(new CollectionFactory()),
	REFLECTION(new ConstructorInstanceFactory(new CachingClassMemberFactory<>(ReflectionUtils::getDeclaredConstructors),
			(e) -> e.getParameterCount() == 0)),
	SERIALIZATION(new ConstructorInstanceFactory(new SerializationConstructorFactory(), (e) -> true)),
	ALLOCATE(new AllocateInstanceFactory());

	private final InstanceFactory instanceFactory;

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		return instanceFactory.canInstantiated(requiredType);
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		return instanceFactory.newInstance(requiredType);
	}
}
