package io.basc.framework.transaction.aop;

import io.basc.framework.core.execution.Function;
import io.basc.framework.transaction.TransactionDefinition;
import io.basc.framework.util.spi.ConfigurableServices;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurableTransactionDefinitionLoader extends ConfigurableServices<TransactionDefinitionLoader>
		implements TransactionDefinitionLoader {
	private TransactionDefinition defaultTransactionDefinition;

	public ConfigurableTransactionDefinitionLoader() {
		setServiceClass(TransactionDefinitionLoader.class);
	}

	@Override
	public TransactionDefinition load(Function function) {
		for (TransactionDefinitionLoader loader : this) {
			TransactionDefinition definition = loader.load(function);
			if (definition != null) {
				return definition;
			}
		}
		return defaultTransactionDefinition;
	}

	@Override
	public boolean isRollback(Function function, Throwable error) {
		return this.allMatch((e) -> e.isRollback(function, error));
	}

}
