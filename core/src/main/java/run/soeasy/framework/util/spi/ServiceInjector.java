package run.soeasy.framework.util.spi;

import run.soeasy.framework.util.exchange.Registration;

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
