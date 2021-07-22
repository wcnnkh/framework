package scw.ibatis;

import org.apache.ibatis.session.SqlSession;

import scw.transaction.Savepoint;
import scw.transaction.Transaction;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;

public class SqlSessionTransactionResource implements TransactionResource {
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

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}

}
