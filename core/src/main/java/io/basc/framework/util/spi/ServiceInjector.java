package io.basc.framework.util.spi;

/**
 * 服务注入
 * 
 * @author shuchaowen
 *
 * @param <S>
 */
@FunctionalInterface
public interface ServiceInjector<S> {
	void inject(S service);
}
