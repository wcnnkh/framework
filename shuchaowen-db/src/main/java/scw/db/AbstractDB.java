package scw.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import scw.core.Consumer;
import scw.core.Init;
import scw.core.utils.ClassUtils;
import scw.core.utils.CollectionUtils;
import scw.core.utils.StringUtils;
import scw.orm.MappingContext;
import scw.orm.sql.TableChange;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.dialect.SqlDialect;
import scw.orm.sql.enums.OperationType;
import scw.orm.sql.support.ORMTemplate;
import scw.serializer.SerializerUtils;
import scw.sql.Sql;
import scw.transaction.sql.SqlTransactionUtils;

public abstract class AbstractDB extends ORMTemplate implements DB, Consumer<AsyncExecute>, DBConfig, Init {

	public SqlDialect getSqlDialect() {
		return getDataBase().getSqlDialect();
	}

	public void init() {
		if (StringUtils.isNotEmpty(getSannerTablePackage())) {
			createTable(getSannerTablePackage());
		}
		getAsyncQueue().addConsumer(this);
	}

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	public void consume(AsyncExecute message) throws Throwable {
		message.execute(this);
	}

	@Override
	public void createTable(Class<?> tableClass, String tableName) {
		createTable(tableClass, tableName, true);
	}

	public void createTable(Class<?> tableClass, String tableName, boolean registerManager) {
		if (registerManager) {
			DBManager.register(tableClass, this);
		}
		super.createTable(tableClass, tableName);
	}

	@Override
	public void createTable(String packageName) {
		createTable(packageName, true);
	}

	public void createTable(String packageName, boolean registerManager) {
		Collection<Class<?>> list = ClassUtils.getClassList(packageName, ClassUtils.getDefaultClassLoader());
		for (Class<?> tableClass : list) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (registerManager) {
				DBManager.register(tableClass, this);
			}

			createTable(tableClass, false);

			// 检查表变更
			checkTableChange(tableClass);
		}
	}

	// 检查表变更
	protected void checkTableChange(Class<?> tableClass) {
		TableChange tableChange = getTableChange(tableClass);
		List<String> addList = new LinkedList<String>();
		if (!CollectionUtils.isEmpty(tableChange.getAddMappingContexts())) {
			for (MappingContext mappingContext : tableChange.getAddMappingContexts()) {
				addList.add(mappingContext.getColumn().getName());
			}
		}

		if (!CollectionUtils.isEmpty(tableChange.getDeleteNames()) || !CollectionUtils.isEmpty(addList)) {
			// 如果存在字段变量
			if(logger.isWarnEnabled()){
				logger.warn("存在字段变更class={}, addList={}, deleteList={}", tableClass.getName(), Arrays.toString(addList.toArray()),
						Arrays.toString(tableChange.getDeleteNames().toArray()));
			}
		}
	}

	@Override
	public Connection getUserConnection() throws SQLException {
		return SqlTransactionUtils.getTransactionConnection(this);
	}

	public void executeSqlByFile(String filePath, boolean lines) throws SQLException {
		Collection<Sql> sqls = DBUtils.getSqlByFile(filePath, lines);
		for (Sql sql : sqls) {
			execute(sql);
		}
	}

	@Override
	public boolean save(Object bean, String tableName) {
		boolean b = super.save(bean, tableName);
		if (b) {
			getCacheManager().save(bean);
		}
		return b;
	}

	@Override
	public boolean update(Object bean, String tableName) {
		boolean b = super.update(bean, tableName);
		if (b) {
			getCacheManager().update(bean);
		}
		return b;
	}

	@Override
	public boolean delete(Object bean, String tableName) {
		boolean b = super.delete(bean, tableName);
		if (b) {
			getCacheManager().delete(bean);
		}
		return b;
	}

	@Override
	public boolean deleteById(String tableName, Class<?> type, Object... params) {
		boolean b = super.deleteById(tableName, type, params);
		if (b) {
			getCacheManager().deleteById(type, params);
		}
		return b;
	}

	@Override
	public boolean saveOrUpdate(Object bean, String tableName) {
		boolean b = super.saveOrUpdate(bean, tableName);
		if (b) {
			getCacheManager().saveOrUpdate(bean);
		}
		return b;
	}

	@Override
	public <T> T getById(String tableName, Class<T> type, Object... params) {
		T t = getCacheManager().getById(type, params);
		if (t == null) {
			if (getCacheManager().isSearchDB(type, params)) {
				t = super.getById(tableName, type, params);
				if (t != null) {
					getCacheManager().save(t);
				}
			}
		}
		return t;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <K, V> Map<K, V> getInIdList(Class<V> type, String tableName, Collection<K> inIds, Object... params) {
		if (inIds == null || inIds.isEmpty()) {
			return Collections.EMPTY_MAP;
		}

		Map<K, V> map = getCacheManager().getInIdList(type, inIds, params);
		if (CollectionUtils.isEmpty(map)) {
			Map<K, V> valueMap = super.getInIdList(type, tableName, inIds, params);
			if (!CollectionUtils.isEmpty(valueMap)) {
				for (Entry<K, V> entry : valueMap.entrySet()) {
					getCacheManager().save(entry.getValue());
				}
			}
			return valueMap;
		}

		if (map.size() == inIds.size()) {
			return map;
		}

		List<K> notFoundList = new ArrayList<K>(inIds.size());
		for (K k : inIds) {
			if (k == null) {
				continue;
			}

			if (map.containsKey(k)) {
				continue;
			}

			notFoundList.add(k);
		}

		if (!CollectionUtils.isEmpty(notFoundList)) {
			Map<K, V> dbMap = super.getInIdList(type, tableName, notFoundList, params);
			if (dbMap == null || dbMap.isEmpty()) {
				return map;
			}

			for (Entry<K, V> entry : dbMap.entrySet()) {
				getCacheManager().save(entry.getValue());
			}
			map.putAll(dbMap);
		}
		return map;
	}

	public final void asyncDelete(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.DELETE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncExecute(Sql... sqls) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Sql sql : sqls) {
			asyncExecute.add(new SqlAsyncExecute(sql));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncSave(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.SAVE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncUpdate(Object... objs) {
		TransactionAsyncExecute asyncExecute = new TransactionAsyncExecute();
		for (Object bean : objs) {
			asyncExecute.add(new BeanAsyncExecute(bean, OperationType.UPDATE));
		}
		asyncExecute(asyncExecute);
	}

	public final void asyncExecute(AsyncExecute asyncExecute) {
		getAsyncQueue().push(SerializerUtils.clone(asyncExecute));
	}
}
