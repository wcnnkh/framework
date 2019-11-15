package scw.db;

public final class DeleteByIdAsyncExecute implements AsyncExecute {
	private static final long serialVersionUID = 1L;
	private final String tableName;
	private final Class<?> type;
	private final Object[] params;

	public DeleteByIdAsyncExecute(String tableName, Class<?> type, Object[] params) {
		this.type = type;
		this.params = params;
		this.tableName = tableName;
	}

	public void execute(DB db) {
		db.deleteById(tableName, type, params);
	}
}
