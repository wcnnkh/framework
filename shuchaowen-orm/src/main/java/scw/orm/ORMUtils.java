package scw.orm;

import java.util.Collection;

import scw.core.GlobalPropertyFactory;
import scw.core.instance.InstanceUtils;
import scw.core.utils.StringUtils;
import scw.orm.support.ObjectOperations;

public final class ORMUtils {
	/**
	 * 默认对象主键的连接符
	 */
	public static final char PRIMARY_KEY_CONNECTOR_CHARACTER = StringUtils
			.parseChar(
					GlobalPropertyFactory.getInstance().getString(
							"orm.primary.key.connector.character"), ':');
	private static final ORMInstanceFactory INSTANCE_FACTORY = InstanceUtils
			.getSystemConfiguration(ORMInstanceFactory.class);
	private static final Collection<Filter> FILTERS = InstanceUtils
			.getSystemConfigurationList(Filter.class);
	private static final ObjectOperations OBJECT_OPERATIONS = InstanceUtils
			.getSystemConfiguration(ObjectOperations.class);

	private ORMUtils() {
	};

	public static ORMInstanceFactory getInstanceFactory() {
		return INSTANCE_FACTORY;
	}

	public static Collection<Filter> getFilters() {
		return FILTERS;
	}

	public static ObjectOperations getObjectOperations() {
		return OBJECT_OPERATIONS;
	}
}