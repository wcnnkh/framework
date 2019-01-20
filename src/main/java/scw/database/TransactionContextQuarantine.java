package scw.database;

import java.util.Collection;
import java.util.LinkedList;

import scw.common.transaction.Transaction;

/**
 * 事务上下文隔离
 * 
 * @author shuchaowen
 *
 */
public class TransactionContextQuarantine {
	private LinkedList<Transaction> transactionList;
	private LinkedList<TransactionSql> sqlList;
	private ContextConfig config;

	public TransactionContextQuarantine(ContextConfig config) {
		this.config = config;
	}

	public void addSql(ConnectionSource connectionSource, SQL sql) {
		if (sqlList == null) {
			sqlList = new LinkedList<TransactionSql>();
		}

		sqlList.add(new TransactionSql(connectionSource, sql));
	}

	public void addSql(ConnectionSource connectionSource, Collection<SQL> sqls) {
		if (sqlList == null) {
			sqlList = new LinkedList<TransactionSql>();
		}

		sqlList.add(new TransactionSql(connectionSource, sqls));
	}

	public void addTransaction(Transaction transaction) {
		if (transactionList == null) {
			transactionList = new LinkedList<Transaction>();
		}
		transactionList.add(transaction);
	}

	public ContextConfig getConfig() {
		return config;
	}

	public LinkedList<Transaction> getTransactionList() {
		return transactionList;
	}

	public LinkedList<TransactionSql> getSqlList() {
		return sqlList;
	}
}
