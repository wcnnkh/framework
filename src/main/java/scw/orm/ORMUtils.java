package scw.orm;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import scw.core.instance.InstanceUtils;
import scw.core.instance.NoArgsInstanceFactory;
import scw.core.instance.SimpleNoArgsInstanceFactory;
import scw.core.utils.ClassUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.StringUtils;
import scw.core.utils.SystemPropertyUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.sql.annotation.Table;
import scw.orm.sql.support.TableColumnFactory;
import scw.orm.support.CacheColumnFactory;
import scw.orm.support.DefaultMapper;
import scw.orm.support.DefaultObjectOperations;
import scw.orm.support.ObjectOperations;

@SuppressWarnings("unchecked")
public final class ORMUtils {
	private static Logger logger = LoggerUtils.getLogger(ORMUtils.class);
	/**
	 * 默认对象主键的连接符
	 */
	public static final char PRIMARY_KEY_CONNECTOR_CHARACTER = StringUtils
			.parseChar(SystemPropertyUtils.getProperty("orm.primary.key.connector.character"), ':');
	private static final ColumnFactory COLUMN_FACTORY;
	private static final Mapper MAPPER;
	private static final ObjectOperations OBJECT_OPERATIONS;

	static {
		COLUMN_FACTORY = new CacheColumnFactory(InstanceUtils.autoNewInstanceBySystemProperty(ColumnFactory.class,
				"orm.column.factory", new TableColumnFactory()));
		Collection<Filter> filters = new LinkedList<Filter>();
		filters.addAll(
				InstanceUtils.autoNewInstancesBySystemProperty(Filter.class, "orm.filters", Collections.EMPTY_LIST));
		NoArgsInstanceFactory noArgsInstanceFactory = InstanceUtils.autoNewInstanceBySystemProperty(
				NoArgsInstanceFactory.class, "orm.instance.factory", new SimpleNoArgsInstanceFactory());
		MAPPER = new DefaultMapper(COLUMN_FACTORY, filters, filters, noArgsInstanceFactory);
		OBJECT_OPERATIONS = new DefaultObjectOperations(MAPPER);
	}

	private ORMUtils() {
	};

	public static ColumnFactory getColumnFactory() {
		return COLUMN_FACTORY;
	}

	public static Mapper getMapper() {
		return MAPPER;
	}

	public static void registerCglibProxyTableBean(String pageName) {
		if (!StringUtils.isEmpty(pageName)) {
			logger.info("register proxy package:{}", pageName);
		}

		for (Class<?> type : ClassUtils.getClassList(pageName, ClassUtils.getDefaultClassLoader())) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			FieldSetterListenUtils.createFieldSetterListenProxyClass(type);
		}
	}

	public static ObjectOperations getObjectOperations() {
		return OBJECT_OPERATIONS;
	}
}