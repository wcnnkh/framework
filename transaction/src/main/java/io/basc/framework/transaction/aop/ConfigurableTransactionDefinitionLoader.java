package io.basc.framework.transaction.aop;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.core.execution.Function;
import io.basc.framework.transaction.TransactionDefinition;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigurableTransactionDefinitionLoader extends ConfigurableServices<TransactionDefinitionLoader>
		implements TransactionDefinitionLoader {
	private TransactionDefinition defaultTransactionDefinition;

	public ConfigurableTransactionDefinitionLoader() {
		super(TransactionDefinitionLoader.class);
	}

	@Override
	public TransactionDefinition load(Function function) {
		for (TransactionDefinitionLoader loader : getServices()) {
			TransactionDefinition definition = loader.load(function);
			if (definition != null) {
				return definition;
			}
		}
		return defaultTransactionDefinition;
	}

	@Override
	public boolean isRollback(Function function, Throwable error) {
		return getServices().allMatch((e) -> e.isRollback(function, error));
	}

}
