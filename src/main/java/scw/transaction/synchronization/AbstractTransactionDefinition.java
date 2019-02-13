package scw.transaction.synchronization;

import scw.transaction.TransactionDefinition;

public abstract class AbstractTransactionDefinition implements TransactionDefinition {

	abstract AbstractTransaction newTransaction(AbstractTransaction old, boolean active);
}
