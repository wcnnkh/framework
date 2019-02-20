package scw.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.XUtils;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.sql.orm.ORMUtils;
import scw.sql.orm.result.DefaultResultSet;

public final class DataBaseUtils {
	private DataBaseUtils() {
	};

	public static void registerCglibProxyTableBean(String pageName) {
		ORMUtils.registerCglibProxyTableBean(pageName);
	}

	public static void iterator(ConnectionFactory connectionSource, Sql sql, scw.common.Iterator<ResultSet> iterator) {
		if (sql == null || connectionSource == null || iterator == null) {
			return;
		}

		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = SqlUtils.createPreparedStatement(connection, sql);
			rs = stmt.executeQuery();
			while (rs.next()) {
				iterator.iterator(rs);
			}
		} catch (Exception e) {
			throw new ShuChaoWenRuntimeException(SqlUtils.getSqlId(sql), e);
		} finally {
			XUtils.close(rs, stmt, connection);
		}
	}

	public static scw.sql.orm.result.ResultSet select(ConnectionFactory connectionSource, Sql sql) {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection connection = null;
		try {
			connection = connectionSource.getConnection();
			stmt = SqlUtils.createPreparedStatement(connection, sql);
			rs = stmt.executeQuery();
			return new DefaultResultSet(rs);
		} catch (SQLException e) {
			throw new ShuChaoWenRuntimeException(SqlUtils.getSqlId(sql), e);
		} finally {
			XUtils.close(rs, stmt, connection);
		}
	}

	public static void execute(ConnectionFactory connectionPool, Collection<Sql> sqls) {
		if (sqls == null || connectionPool == null) {
			throw new NullPointerException();
		}

		Iterator<Sql> iterator = sqls.iterator();
		if (sqls.size() == 1) {
			Sql sql = iterator.next();
			PreparedStatement stmt = null;
			Connection connection = null;
			try {
				connection = connectionPool.getConnection();
				stmt = SqlUtils.createPreparedStatement(connection, sql);
				stmt.execute();
			} catch (SQLException e) {
				throw new ShuChaoWenRuntimeException(SqlUtils.getSqlId(sql), e);
			} finally {
				XUtils.close(stmt, connection);
			}
		} else {
			SQLTransaction sqlTransaction = new SQLTransaction(connectionPool);
			while (iterator.hasNext()) {
				sqlTransaction.addSql(iterator.next());
			}
			sqlTransaction.execute();
		}
	}

	public static String getLikeValue(String likeValue) {
		if (likeValue == null || likeValue.length() == 0) {
			return "%";// 注意：这会忽略空
		}

		return "%" + likeValue + "%";
	}
}
