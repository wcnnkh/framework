package io.basc.framework.util.spi;

import io.basc.framework.util.Receipt;

/**
 * 可配置的
 * 
 * @author shuchaowen
 *
 */
public interface Configurable {
	/**
	 * 执行配置
	 * 
	 * @param discovery
	 * @return
	 */
	Receipt doConfigure(ServiceLoaderDiscovery discovery);

	/**
	 * 执行原生的spi配置
	 * 
	 * @return
	 */
	default Receipt doNativeConfigure() {
		return doConfigure(NativeServiceLoader::load);
	}
}
