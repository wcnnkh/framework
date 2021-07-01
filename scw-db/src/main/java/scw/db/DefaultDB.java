package scw.db;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import scw.context.ClassesLoaderFactory;
import scw.context.support.DefaultClassesLoaderFactory;
import scw.core.Assert;
import scw.core.type.scanner.DefaultClassScanner;
import scw.core.utils.CollectionUtils;
import scw.logger.Logger;
import scw.logger.LoggerFactory;
import scw.mapper.Field;
import scw.orm.sql.DefaultSqlTemplate;
import scw.orm.sql.SqlDialect;
import scw.orm.sql.TableChanges;
import scw.orm.sql.annotation.Table;
import scw.sql.ConnectionFactory;

public class DefaultDB extends DefaultSqlTemplate implements DB {
	private static Logger logger = LoggerFactory.getLogger(DefaultDB.class);
	private boolean checkTableChange = true;
	private ClassesLoaderFactory classesLoaderFactory = new DefaultClassesLoaderFactory(new DefaultClassScanner(),
			false, null);

	public DefaultDB(ConnectionFactory connectionFactory, SqlDialect sqlDialect) {
		super(connectionFactory, sqlDialect);
	}

	public ClassesLoaderFactory getClassesLoaderFactory() {
		return classesLoaderFactory;
	}

	public void setClassesLoaderFactory(ClassesLoaderFactory classesLoaderFactory) {
		Assert.requiredArgument(classesLoaderFactory != null, "classesLoaderFactory");
		this.classesLoaderFactory = classesLoaderFactory;
	}

	public boolean isCheckTableChange() {
		return checkTableChange;
	}

	public void setCheckTableChange(boolean checkTableChange) {
		this.checkTableChange = checkTableChange;
	}

	public boolean createTable(Class<?> tableClass, boolean registerManager) {
		return createTable(tableClass, null, registerManager);
	}

	@Override
	public boolean createTable(String tableName, Class<?> entityClass) {
		DBManager.register(entityClass, this);
		return super.createTable(tableName, entityClass);
	}

	public boolean createTable(Class<?> tableClass, String tableName, boolean registerManager) {
		if (registerManager) {
			DBManager.register(tableClass, this);
		}

		boolean b = super.createTable(tableName, tableClass);
		// 检查表变更
		if (isCheckTableChange()) {
			checkTableChange(tableClass);
		}
		return b;
	}

	public void createTables(String packageName, boolean registerManager) {
		for (Class<?> tableClass : getClassesLoaderFactory().getClassesLoader(packageName)) {
			Table table = tableClass.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			if (registerManager) {
				DBManager.register(tableClass, this);
			}

			createTable(tableClass, false);
		}
	}

	// 检查表变更
	protected void checkTableChange(Class<?> tableClass) {
		TableChanges tableChange = getTableChanges(tableClass);
		List<String> addList = new LinkedList<String>();
		if (!CollectionUtils.isEmpty(tableChange.getAddColumnss())) {
			for (Field column : tableChange.getAddColumnss()) {
				addList.add(column.getGetter().getName());
			}
		}

		if (!CollectionUtils.isEmpty(tableChange.getDeleteColumns()) || !CollectionUtils.isEmpty(addList)) {
			// 如果存在字段变更
			if (logger.isWarnEnabled()) {
				logger.warn("There are field changes class={}, addList={}, deleteList={}", tableClass.getName(),
						Arrays.toString(addList.toArray()), Arrays.toString(tableChange.getDeleteColumns().toArray()));
			}
		}
	}
}
