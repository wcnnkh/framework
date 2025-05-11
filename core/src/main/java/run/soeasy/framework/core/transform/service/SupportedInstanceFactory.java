package run.soeasy.framework.core.transform.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.CollectionFactory;
import run.soeasy.framework.core.reflect.AllocateInstanceFactory;
import run.soeasy.framework.core.reflect.ConstructorInstanceFactory;
import run.soeasy.framework.core.reflect.ReflectionUtils;
import run.soeasy.framework.core.reflect.SerializationConstructorFactory;
import run.soeasy.framework.core.type.CachingClassMemberFactory;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.ResolvableType;

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
