package io.basc.framework.ibatis;

import io.basc.framework.lang.Nullable;
import io.basc.framework.transaction.Transaction;
import io.basc.framework.util.stream.CallableProcessor;
import io.basc.framework.util.stream.Processor;

import org.apache.ibatis.session.SqlSession;

public interface OpenSessionProcessor
		extends Processor<Transaction, SqlSession, IbatisException>, CallableProcessor<SqlSession, IbatisException> {
	SqlSession process(@Nullable Transaction source) throws IbatisException;

	@Override
	default SqlSession process() throws IbatisException {
		return process((Transaction) null);
	}
}
