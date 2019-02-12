package scw.transaction.synchronization;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.sql.TransactionSql;

public class ConnectionFactoryTransactionInfo {
	private Map<ConnectionFactory, Connection> connectionMap;
	private LinkedList<TransactionSql> sqls;//延迟执行
	
	public Connection getConnection(ConnectionFactory connectionFactory) throws SQLException{
		Connection connection;
		if(connectionMap == null){
			connectionMap = new HashMap<ConnectionFactory, Connection>();
			connection = SqlUtils.newProxyConnection(connectionFactory);
			connectionMap.put(connectionFactory, connection);
		}else{
			connection = connectionMap.get(connectionFactory);
			if(connection == null){
				connection = SqlUtils.newProxyConnection(connectionFactory);
				connectionMap.put(connectionFactory, connection);
			}
		}
		return connection;
	}
	
	public void addSqls(ConnectionFactory connectionFactory, Sql ...sqls){
		if(this.sqls == null){
			this.sqls = new LinkedList<TransactionSql>();
		}
		this.sqls.add(new TransactionSql(connectionFactory, Arrays.asList(sqls)));
	}
}
