package io.basc.framework.util.register;

import java.util.Arrays;

import io.basc.framework.util.Receipt;

public interface ServiceRegistry<S> extends Registry<S> {
	/**
	 * 取消登记
	 * 
	 * @param service
	 * @return
	 */
	default Receipt deregister(S service) {
		return deregisters(Arrays.asList(service));
	}

	/**
	 * 只要有一个成功就是成功
	 * 
	 * @param services
	 * @return
	 */
	Receipt deregisters(Iterable<? extends S> services);
}
