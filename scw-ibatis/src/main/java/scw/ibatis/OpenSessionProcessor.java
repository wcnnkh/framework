package scw.ibatis;

import org.apache.ibatis.session.SqlSession;

import scw.lang.Nullable;
import scw.transaction.Transaction;
import scw.util.stream.CallableProcessor;
import scw.util.stream.Processor;

public interface OpenSessionProcessor
		extends Processor<Transaction, SqlSession, IbatisException>, CallableProcessor<SqlSession, IbatisException> {
	SqlSession process(@Nullable Transaction source) throws IbatisException;

	@Override
	default SqlSession process() throws IbatisException {
		return process((Transaction) null);
	}
}
