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
	 * 配置
	 * 
	 * @param discovery
	 * @return
	 */
	Receipt configure(@NonNull ProviderFactory discovery);

	/**
	 * 进行系统配置
	 * 
	 * @return
	 */
	default Receipt configure() {
		return configure(SystemProviderFactory.getInstance());
	}
}
