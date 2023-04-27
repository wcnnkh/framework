package io.basc.framework.core.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.core.DefaultStructure;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Elements;
import io.basc.framework.util.StringUtils;

public final class Methods extends MemberStructure<Method, Methods> {
	private final Function<? super MemberStructure<Method, Methods>, ? extends Methods> memberDecorator = (
			source) -> new Methods(source);

	public Methods(Class<?> source, Function<? super Class<?>, ? extends Method[]> processor) {
		super(source, processor);
	}

	private Methods(DefaultStructure<Method> members) {
		super(members);
	}

	@Override
	public Function<? super MemberStructure<Method, Methods>, ? extends Methods> getMemberStructureDecorator() {
		return memberDecorator;
	}

	public Methods filterParameters(Predicate<? super Elements<Parameter>> predicate) {
		Assert.requiredArgument(predicate != null, "predicate");
		return filter((method) -> {
			Parameter[] parameters = method.getParameters();
			return predicate.test(Elements.forArray(parameters));
		});
	}

	public Methods filterParameterTypes(Elements<? extends Class<?>> parameterTypes) {
		Assert.requiredArgument(parameterTypes != null, "parameterTypes");
		return filter((method) -> {
			Class<?>[] types = method.getParameterTypes();
			return Elements.forArray(types).equals(parameterTypes, ClassUtils::isAssignable);
		});
	}

	/**
	 * 没有参数的方法
	 * 
	 * @param name
	 * @return
	 */
	@Nullable
	public Method find(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return find(name, Elements.empty());
	}

	@Nullable
	public Method find(String name, Class<?>... parameterTypes) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return find(name, parameterTypes == null ? Elements.empty() : Elements.forArray(parameterTypes));
	}

	@Nullable
	public Method find(String name, Elements<? extends Class<?>> parameterTypes) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		Assert.requiredArgument(parameterTypes != null, "parameterTypes");
		for (Method method : getElements()) {
			if (!method.getName().equals(name)) {
				continue;
			}

			if (parameterTypes != null && !parameterTypes.equals(Elements.forArray(method.getParameterTypes()),
					ClassUtils::isAssignable)) {
				continue;
			}

			return method;
		}
		return null;
	}
}
