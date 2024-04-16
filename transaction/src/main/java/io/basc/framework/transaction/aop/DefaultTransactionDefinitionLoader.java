package io.basc.framework.transaction.aop;

import io.basc.framework.beans.factory.spi.SPI;

class DefaultTransactionDefinitionLoader extends ConfigurableTransactionDefinitionLoader {
	DefaultTransactionDefinitionLoader() {
		registerServiceLoader(SPI.global().getServiceLoader(TransactionDefinitionLoader.class));
	}
}
