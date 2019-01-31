package scw.jdbc.transaction;

import java.util.Map;

import com.rabbitmq.client.ConnectionFactory;

import scw.transaction.tcc.TccTransactionCollection;

public class TccTransactionContext {
	private Map<ConnectionFactory, ConnectionHolder> map;
	private TccTransactionCollection tccTransactionCollection;
	private TccTransactionContext parent;
	
	public TccTransactionContext(TccTransactionContext parent){
		
	}
}
