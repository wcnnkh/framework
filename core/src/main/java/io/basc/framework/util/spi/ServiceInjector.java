package io.basc.framework.util.spi;

import io.basc.framework.util.registry.Registration;

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
