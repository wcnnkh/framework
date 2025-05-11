package run.soeasy.framework.core.reflect;

import java.lang.reflect.Constructor;
import java.util.function.Predicate;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.type.ClassMemberFactory;
import run.soeasy.framework.core.type.InstanceFactory;
import run.soeasy.framework.core.type.ResolvableType;

/**
 * 基于构造器的实例工厂
 * 
 * @author wcnnkh
 *
 */
@RequiredArgsConstructor
public class ConstructorInstanceFactory implements InstanceFactory {
	@NonNull
	private final ClassMemberFactory<Constructor<?>> constructorFactory;
	@NonNull
	private final Predicate<? super Constructor<?>> predicate;

	@Override
	public boolean canInstantiated(@NonNull ResolvableType requiredType) {
		for (Constructor<?> constructor : constructorFactory.getClassMemberProvider(requiredType.getRawType())) {
			if (predicate.test(constructor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object newInstance(@NonNull ResolvableType requiredType) {
		for (Constructor<?> constructor : constructorFactory.getClassMemberProvider(requiredType.getRawType())) {
			if (predicate.test(constructor)) {
				return ReflectionUtils.newInstance(constructor);
			}
		}
		throw new UnsupportedOperationException(requiredType.toString());
	}
}
