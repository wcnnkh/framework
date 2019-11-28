package scw.orm;

import scw.core.utils.ClassUtils;
import scw.core.utils.FieldSetterListenUtils;
import scw.core.utils.StringUtils;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.orm.sql.annotation.Table;

public final class ORMUtils {
	private static Logger logger = LoggerUtils.getLogger(ORMUtils.class);

	private ORMUtils() {
	};

	public static void registerCglibProxyTableBean(String pageName) {
		if (!StringUtils.isEmpty(pageName)) {
			logger.info("register proxy package:{}", pageName);
		}

		for (Class<?> type : ClassUtils.getClassList(pageName)) {
			Table table = type.getAnnotation(Table.class);
			if (table == null) {
				continue;
			}

			FieldSetterListenUtils.createFieldSetterListenProxyClass(type);
		}
	}
}
