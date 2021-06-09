package scw.sql;

import java.sql.Connection;

public class SqlPreparedStatementCreator implements PreparedStatementCreator<PreparedStatement>{
	private final boolean sql;
	
	public SqlPreparedStatementCreator(boolean sql) {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public PreparedStatement create(Connection connection, String sql) {
		// TODO Auto-generated method stub
		return null;
	}

}
