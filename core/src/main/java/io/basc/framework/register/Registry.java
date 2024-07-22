package io.basc.framework.register;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.element.ServiceLoader;

/**
 * 定义一个注册表
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
public interface Registry<T extends Registration> extends Registration, ServiceLoader<T> {

	Registration register(T registration);

	default Registration registers(Iterable<? extends T> registrations) throws RegistrationException {
		return Registration.registers(registrations, this::register);
	}

	/**
	 * 获取注册表的条目
	 */
	@Override
	Elements<T> getServices();

	/**
	 * 重新加载注册表
	 */
	@Override
	void reload();

	/**
	 * 映射每个条目
	 * 
	 * @param <R>
	 * @param mapper
	 * @return
	 */
	<R extends Registration> Registry<R> map(Function<? super T, ? extends R> mapper);

	/**
	 * 为注册表添加行为
	 */
	@Override
	Registry<T> and(Registration registration);
}
