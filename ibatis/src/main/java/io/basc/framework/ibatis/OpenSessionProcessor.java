package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSession;

import io.basc.framework.lang.Nullable;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.util.Function;
import io.basc.framework.util.Source;

public interface OpenSessionProcessor
		extends Function<Transaction, SqlSession, IbatisException>, Source<SqlSession, IbatisException> {
	SqlSession process(@Nullable Transaction source) throws IbatisException;

	@Override
	default SqlSession get() throws IbatisException {
		return process((Transaction) null);
	}
}
