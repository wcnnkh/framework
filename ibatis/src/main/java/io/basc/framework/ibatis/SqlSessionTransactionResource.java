package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSession;

import io.basc.framework.transaction.Synchronization;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.transaction.TransactionStatus;

public class SqlSessionTransactionResource implements Synchronization {
	private final OpenSessionProcessor openSessionProcessor;
	private final Transaction transaction;
	private SqlSession sqlSession;

	public SqlSessionTransactionResource(Transaction transaction, OpenSessionProcessor openSessionProcessor) {
		this.transaction = transaction;
		this.openSessionProcessor = openSessionProcessor;
	}

	public SqlSession getSqlSession() {
		if (sqlSession == null) {
			sqlSession = openSessionProcessor.process(transaction);
			sqlSession = MybatisUtils.proxySqlSession(sqlSession);
		}
		return sqlSession;
	}

	public void commit() throws Throwable {
		if (sqlSession != null) {
			sqlSession.commit();
		}
	}

	public void rollback() {
		if (sqlSession != null) {
			sqlSession.rollback();
		}
	}

	public void complete() {
		if (sqlSession != null) {
			MybatisUtils.closeSqlSessionProxy(sqlSession);
		}
	}

	@Override
	public void beforeCompletion() throws Throwable {
		commit();
	}

	@Override
	public void afterCompletion(TransactionStatus status) {
		if (status.equals(TransactionStatus.ROLLING_BACK)) {
			rollback();
		}

		if (status.equals(TransactionStatus.COMPLETED)) {
			complete();
		}
	}

}
