package scw.common.transaction;

import scw.common.Context;

/**
 * 事务的上下文管理器
 * 
 * @author shuchaowen
 */
public final class TransactionContext extends Context<TransactionCollection> {
	/**
	 * 理论上一个应用程序只要一个事务管理器
	 */
	private static TransactionContext instance = new TransactionContext();

	/**
	 * 获取一个事务上下文的单例
	 * 
	 * @return
	 */
	public static TransactionContext getInstance() {
		return instance;
	}

	public void execute(Transaction transaction) {
		if (transaction == null) {
			return;
		}

		TransactionCollection transactionList = getValue();
		if (transactionList == null) {// 如果未使用事务
			AbstractTransaction.transaction(transaction);
		} else {
			transactionList.add(transaction);
		}
	}

	@Override
	protected void firstBegin() {
		setValue(new TransactionCollection());
	}

	@Override
	protected void lastCommit() {
		TransactionCollection transaction = getValue();
		if (transaction != null) {
			AbstractTransaction.transaction(transaction);
		}
	}
}