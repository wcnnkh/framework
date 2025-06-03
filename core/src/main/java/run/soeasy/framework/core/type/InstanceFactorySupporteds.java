package run.soeasy.framework.core.type;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * 已支持的实例工厂
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
public enum InstanceFactorySupporteds implements InstanceFactory {
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
