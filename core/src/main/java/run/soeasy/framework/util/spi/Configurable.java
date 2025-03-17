package run.soeasy.framework.util.spi;

import lombok.NonNull;
import run.soeasy.framework.util.exchange.Receipt;

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
	Receipt doConfigure(@NonNull ServiceLoaderDiscovery discovery);

	/**
	 * 执行原生的spi配置
	 * 
	 * @return
	 */
	default Receipt doNativeConfigure() {
		return doConfigure(NativeServiceLoader::load);
	}
}
