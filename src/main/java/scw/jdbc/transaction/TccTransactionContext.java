package scw.jdbc.transaction;

import java.util.Map;

import com.rabbitmq.client.ConnectionFactory;

import scw.transaction.tcc.TccTransactionItemCollection;

public class TccTransactionContext {
	private Map<ConnectionFactory, ConnectionHolder> map;
	private TccTransactionItemCollection tccTransactionItemCollection;
	private TccTransactionContext parent;
	
	public TccTransactionContext(TccTransactionContext parent){
		
	}
}
