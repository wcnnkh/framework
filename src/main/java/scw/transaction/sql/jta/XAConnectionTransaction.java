package scw.transaction.sql.jta;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

import scw.transaction.Isolation;
import scw.transaction.TransactionDefinition;
import scw.transaction.TransactionException;
import scw.transaction.sql.AbstractConnectionTransactionResource;

public class XAConnectionTransaction extends AbstractConnectionTransactionResource {
	private final XADataSource xaDataSource;
	private XAConnection xaConnection;
	private XAResource xaResource;
	private Connection connection;

	public XAConnectionTransaction(XADataSource xaDataSource, TransactionDefinition transactionDefinition,
			boolean active) {
		super(transactionDefinition, active);
		this.xaDataSource = xaDataSource;
	}

	private void init() throws SQLException, XAException {
		if (xaConnection == null) {
			xaConnection = xaDataSource.getXAConnection();
		}

		if (xaResource == null) {
			xaResource = xaConnection.getXAResource();
			xaResource.setTransactionTimeout(getTransactionDefinition().getTimeout());
		}

		if (connection == null) {
			connection = xaConnection.getConnection();
			connection.setAutoCommit(!isActive());
			if (getTransactionDefinition().isReadOnly()) {
				connection.setReadOnly(getTransactionDefinition().isReadOnly());
			}

			Isolation isolation = getTransactionDefinition().getIsolation();
			if (isolation != Isolation.DEFAULT) {
				connection.setTransactionIsolation(isolation.getLevel());
			}
		}
	}

	public Connection getConnection() throws SQLException {
		if (connection == null) {
			try {
				init();
			} catch (XAException e) {
				throw new TransactionException(e);
			}
		}
		return connection;
	}

	public void rollback() {
		// TODO Auto-generated method stub

	}

	public void end() {
		// TODO Auto-generated method stub

	}
}
