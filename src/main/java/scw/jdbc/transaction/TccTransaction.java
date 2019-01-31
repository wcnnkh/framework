package scw.jdbc.transaction;

import java.util.Map;

import com.rabbitmq.client.ConnectionFactory;

public class TccTransaction {
	private Map<ConnectionFactory, ConnectionHolder> map;
}
