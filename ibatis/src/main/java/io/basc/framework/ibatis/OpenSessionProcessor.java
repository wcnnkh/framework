package io.basc.framework.ibatis;

import org.apache.ibatis.session.SqlSession;

import io.basc.framework.lang.Nullable;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.util.function.Processor;
import io.basc.framework.util.function.Source;

public interface OpenSessionProcessor
		extends Processor<Transaction, SqlSession, IbatisException>, Source<SqlSession, IbatisException> {
	SqlSession process(@Nullable Transaction source) throws IbatisException;

	@Override
	default SqlSession get() throws IbatisException {
		return process((Transaction) null);
	}
}
