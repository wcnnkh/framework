package scw.db;

import java.sql.Connection;
import java.sql.SQLException;

import scw.db.database.DataBase;
import scw.sql.orm.AbstractORMTemplate;
import scw.sql.orm.SqlFormat;
import scw.transaction.sql.ConnectionFactory;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class DB extends AbstractORMTemplate implements ConnectionFactory, AutoCloseable {
	public abstract DataBase getDataBase();

	@Override
	public SqlFormat getSqlFormat() {
		return getDataBase().getDataBaseType().getSqlFormat();
	}

	@Override
	protected Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}
}
