package scw.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import scw.common.utils.XUtils;
import scw.sql.ConnectionFactory;
import scw.sql.Sql;
import scw.sql.SqlUtils;
import scw.sql.orm.ORMUtils;

public final class DataBaseUtils {
	private DataBaseUtils() {
	};

	/**
	 * ORMUtils.registerCglibProxyTableBean
	 * @param pageName
	 */
	@Deprecated
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
			throw new RuntimeException(SqlUtils.getSqlId(sql), e);
		} finally {
			XUtils.close(rs, stmt, connection);
		}
	}

	public static String getLikeValue(String likeValue) {
		if (likeValue == null || likeValue.length() == 0) {
			return "%";// 注意：这会忽略空
		}

		return "%" + likeValue + "%";
	}
}
