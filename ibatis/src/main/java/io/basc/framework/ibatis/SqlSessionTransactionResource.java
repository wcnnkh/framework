package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSession;

import io.basc.framework.transaction.Resource;
import io.basc.framework.transaction.Transaction;

public class SqlSessionTransactionResource implements Resource {
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

	public void close() {
		if (sqlSession != null) {
			MybatisUtils.closeSqlSessionProxy(sqlSession);
		}
	}

}
