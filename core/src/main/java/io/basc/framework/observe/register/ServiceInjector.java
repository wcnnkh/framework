package io.basc.framework.observe.register;

import io.basc.framework.util.Registration;

/**
 * 服务注入
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
@FunctionalInterface
public interface ServiceInjector<S> {
	Registration inject(S service);
}
