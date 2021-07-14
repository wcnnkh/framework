package scw.ibatis;

import java.util.function.Supplier;

import org.apache.ibatis.session.SqlSession;

import scw.transaction.Savepoint;
import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;

class MybatisTransactionResource implements TransactionResource {
	private final Supplier<SqlSession> sqlSessionSupplier;
	private SqlSession sqlSession;

	public MybatisTransactionResource(Supplier<SqlSession> sqlSessionSupplier) {
		this.sqlSessionSupplier = sqlSessionSupplier;
	}

	public SqlSession getSqlSession() {
		if (sqlSession == null) {
			sqlSession = sqlSessionSupplier.get();
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
