package run.soeasy.framework.util.reflect;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.function.Function;
import java.util.function.Predicate;

import run.soeasy.framework.util.Assert;
import run.soeasy.framework.util.ClassUtils;
import run.soeasy.framework.util.Members;
import run.soeasy.framework.util.StringUtils;
import run.soeasy.framework.util.collection.Elements;

public final class Methods extends ReflectionMembers<Method, Methods> {
	private final Function<? super ReflectionMembers<Method, Methods>, ? extends Methods> memberDecorator = (
			source) -> new Methods(source);

	public Methods(Class<?> source, Function<? super Class<?>, ? extends Method[]> processor) {
		super(source, processor);
	}

	private Methods(Members<Method> members) {
		super(members);
	}

	@Override
	public Function<? super ReflectionMembers<Method, Methods>, ? extends Methods> getMemberStructureDecorator() {
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
	
	public Method find(String name) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return find(name, Elements.empty());
	}

	
	public Method find(String name, Class<?>... parameterTypes) {
		Assert.requiredArgument(StringUtils.hasText(name), "name");
		return find(name, parameterTypes == null ? Elements.empty() : Elements.forArray(parameterTypes));
	}

	
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
