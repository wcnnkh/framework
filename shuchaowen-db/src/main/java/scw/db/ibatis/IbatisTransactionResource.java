package scw.db.ibatis;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import scw.transaction.TransactionException;
import scw.transaction.TransactionResource;
import scw.transaction.savepoint.Savepoint;

public class IbatisTransactionResource implements TransactionResource {
	private final SqlSessionFactory sqlSessionFactory;
	private final boolean isActive;
	private SqlSession sqlSession;

	public IbatisTransactionResource(SqlSessionFactory sqlSessionFactory,
			boolean isActive) {
		this.sqlSessionFactory = sqlSessionFactory;
		this.isActive = isActive;
	}

	public SqlSession getSqlSession() {
		if (sqlSession == null) {
			sqlSession = IbatisUtils.proxySqlSession(sqlSessionFactory
					.openSession(!isActive));
		}
		return sqlSession;
	}

	public void process() throws Throwable {
		if(sqlSession != null){
			sqlSession.commit();
		}
	}

	public void rollback() {
		if(sqlSession != null){
			sqlSession.rollback();
		}
	}

	public void end() {
		if(sqlSession != null){
			IbatisUtils.closeSqlSessionProxy(sqlSession);
		}
	}

	public Savepoint createSavepoint() throws TransactionException {
		return null;
	}

}
