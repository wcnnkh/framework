package run.soeasy.framework.util.logging;

import run.soeasy.framework.util.exchange.Receipt;

public final class LogManager {
	private static final ConfigurableLoggerFactory CONFIGURABLE = new ConfigurableLoggerFactory();

	private static volatile Receipt receipt;

	public static Receipt getReceipt() {
		if (receipt == null) {
			synchronized (CONFIGURABLE) {
				if (receipt == null) {
					receipt = reloadReceipt();
				}
			}
		}
		return receipt;
	}

	public static Receipt reloadReceipt() {
		synchronized (CONFIGURABLE) {
			Receipt receipt = CONFIGURABLE.doNativeConfigure();
			LogManager.receipt = receipt;
			return receipt;
		}
	}

	public static ConfigurableLoggerFactory getConfigurable() {
		return CONFIGURABLE;
	}

	public static Logger getLogger(Class<?> clazz) {
		return getLogger(clazz.getName());
	}

	public static Logger getLogger(String name) {
		Receipt receipt = getReceipt();
		if (!receipt.isSuccess()) {
			reloadReceipt();
		}
		return CONFIGURABLE.getLogger(name);
	}

	private LogManager() {
		throw new UnsupportedOperationException();
	};
}
