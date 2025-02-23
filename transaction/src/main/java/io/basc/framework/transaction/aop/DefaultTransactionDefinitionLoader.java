package io.basc.framework.transaction.aop;

class DefaultTransactionDefinitionLoader extends ConfigurableTransactionDefinitionLoader {
	DefaultTransactionDefinitionLoader() {
		doNativeConfigure();
	}
}
