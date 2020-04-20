package scw.db.hibernate;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import scw.core.Pagination;
import scw.core.utils.IteratorCallback;
import scw.core.utils.IteratorCallback.Row;
import scw.db.AsyncExecute;
import scw.db.cache.CacheManager;
import scw.orm.sql.ResultMapping;
import scw.orm.sql.ResultSet;
import scw.orm.sql.TableChange;
import scw.sql.ResultSetCallback;
import scw.sql.ResultSetMapper;
import scw.sql.RowCallback;
import scw.sql.RowMapper;
import scw.sql.Sql;
import scw.sql.SqlException;

public abstract class AbstractHibernateDB implements HibernateDB{

	public CacheManager getCacheManager() {
		// TODO Auto-generated method stub
		return null;
	}

	public void createTable(Class<?> tableClass, boolean registerManager) {
	}

	public void createTable(Class<?> tableClass, String tableName,
			boolean registerManager) {
		// TODO Auto-generated method stub
		
	}

	public void createTable(String packageName, boolean registerManager) {
		// TODO Auto-generated method stub
		
	}

	public void executeSqlByFile(String filePath, boolean lines)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void asyncExecute(AsyncExecute asyncExecute) {
		// TODO Auto-generated method stub
		
	}

	public void asyncSave(Object... objs) {
		// TODO Auto-generated method stub
		
	}

	public void asyncUpdate(Object... objs) {
		// TODO Auto-generated method stub
		
	}

	public void asyncDelete(Object... objs) {
		// TODO Auto-generated method stub
		
	}

	public void asyncSaveOrUpdate(Object... objs) {
		// TODO Auto-generated method stub
		
	}

	public void asyncExecute(Sql... sqls) {
		// TODO Auto-generated method stub
		
	}

	public <T> T getById(Class<? extends T> type, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getById(String tableName, Class<? extends T> type,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> getByIdList(Class<? extends T> type, Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> getByIdList(String tableName, Class<? extends T> type,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<? extends V> type,
			String tableName, Collection<? extends K> inPrimaryKeys,
			Object... primaryKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	public <K, V> Map<K, V> getInIdList(Class<? extends V> type,
			Collection<? extends K> inPrimaryKeys, Object... primaryKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	public ResultSet select(Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> select(Class<? extends T> type, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T selectOne(Class<? extends T> type, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T selectOne(Class<? extends T> type, Sql sql, T defaultValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean save(Object bean) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean save(Object bean, String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(Object bean) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean update(Object bean, String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean delete(Object bean) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean delete(Object bean, String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteById(Class<?> type, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean saveOrUpdate(Object bean) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean saveOrUpdate(Object bean, String tableName) {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass,
			String tableName, String idField) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getMaxValue(Class<? extends T> type, Class<?> tableClass,
			String idField) {
		// TODO Auto-generated method stub
		return null;
	}

	public void createTable(Class<?> tableClass) {
		// TODO Auto-generated method stub
		
	}

	public void createTable(Class<?> tableClass, String tableName) {
		// TODO Auto-generated method stub
		
	}

	public void createTable(String packageName) {
		// TODO Auto-generated method stub
		
	}

	public <T> Pagination<List<T>> select(Class<? extends T> type, long page,
			int limit, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pagination<ResultSet> select(long page, int limit, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public Pagination<ResultSet> select(int page, int limit, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> Pagination<List<T>> select(Class<? extends T> type, int page,
			int limit, Sql sql) {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> void iterator(Class<? extends T> tableClass,
			IteratorCallback<T> iterator) {
		// TODO Auto-generated method stub
		
	}

	public void iterator(Sql sql, IteratorCallback<ResultMapping> iterator) {
		// TODO Auto-generated method stub
		
	}

	public <T> void iterator(Sql sql, Class<? extends T> type,
			IteratorCallback<T> iterator) {
		// TODO Auto-generated method stub
		
	}

	public <T> void query(Class<? extends T> tableClass,
			IteratorCallback<Row<T>> iterator) {
		// TODO Auto-generated method stub
		
	}

	public void query(Sql sql, IteratorCallback<Row<ResultMapping>> iterator) {
		// TODO Auto-generated method stub
		
	}

	public <T> void query(Sql sql, Class<? extends T> type,
			IteratorCallback<Row<T>> iterator) {
		// TODO Auto-generated method stub
		
	}

	public TableChange getTableChange(Class<?> tableClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public TableChange getTableChange(Class<?> tableClass, String tableName) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean execute(Sql sql) throws SqlException {
		// TODO Auto-generated method stub
		return false;
	}

	public void query(Sql sql, ResultSetCallback resultSetCallback)
			throws SqlException {
		// TODO Auto-generated method stub
		
	}

	public void query(Sql sql, RowCallback rowCallback) throws SqlException {
		// TODO Auto-generated method stub
		
	}

	public <T> T query(Sql sql, ResultSetMapper<T> resultSetMapper)
			throws SqlException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> List<T> query(Sql sql, RowMapper<T> rowMapper)
			throws SqlException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Object[]> query(Sql sql) throws SqlException {
		// TODO Auto-generated method stub
		return null;
	}

	public int update(Sql sql) throws SqlException {
		// TODO Auto-generated method stub
		return 0;
	}

	public int[] batch(Collection<String> sqls) throws SqlException {
		// TODO Auto-generated method stub
		return null;
	}

	public int[] batch(String sql, Collection<Object[]> batchArgs)
			throws SqlException {
		// TODO Auto-generated method stub
		return null;
	}

}
