package io.basc.framework.db;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import io.basc.framework.context.ClassesLoader;
import io.basc.framework.context.ClassesLoaderFactory;
import io.basc.framework.context.support.DefaultClassesLoaderFactory;
import io.basc.framework.core.type.scanner.DefaultClassScanner;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.mapper.Field;
import io.basc.framework.sql.ConnectionFactory;
import io.basc.framework.sql.orm.SqlDialect;
import io.basc.framework.sql.orm.TableChanges;
import io.basc.framework.sql.orm.annotation.Table;
import io.basc.framework.sql.orm.support.DefaultSqlTemplate;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;

public class DefaultDB extends DefaultSqlTemplate implements DB {
	private static Logger logger = LoggerFactory.getLogger(DefaultDB.class);
	private boolean checkTableChange = true;
	private ClassesLoaderFactory classesLoaderFactory = new DefaultClassesLoaderFactory(new DefaultClassScanner());

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

	public void createTable(Class<?> tableClass, boolean registerManager) {
		createTable(tableClass, null, registerManager);
	}

	@Override
	public void createTable(Class<?> entityClass, String tableName) {
		DBManager.register(entityClass, this);
		super.createTable(entityClass, tableName);
	}

	public void createTable(Class<?> tableClass, String tableName, boolean registerManager) {
		if (registerManager) {
			DBManager.register(tableClass, this);
		}

		super.createTable(tableClass, tableName);
		// 检查表变更
		if (isCheckTableChange()) {
			checkTableChange(tableClass);
		}
	}

	public void createTables(String packageName, boolean registerManager) {
		ClassesLoader classesLoader = getClassesLoaderFactory().getClassesLoader(packageName, (e, m) -> {
			return e.getAnnotationMetadata().hasAnnotation(Table.class.getName());
		});

		for (Class<?> tableClass : classesLoader) {
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
