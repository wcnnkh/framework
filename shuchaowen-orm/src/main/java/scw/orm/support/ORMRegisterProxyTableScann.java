package scw.orm.support;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryLifeCycle;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.orm.ORMUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order = Integer.MAX_VALUE)
public final class ORMRegisterProxyTableScann implements BeanFactoryLifeCycle {

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
	}

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		String ormScanPackageName = propertyFactory.getString("orm.scan");
		if (StringUtils.isNotEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(parseRootPackage(GlobalPropertyFactory
					.getInstance().getBasePackageName()));
		}
	}

	public static String parseRootPackage(String packageName) {
		String[] arr = StringUtils.split(packageName, '.');
		if (arr.length < 1) {
			return null;
		} else if (arr.length == 1) {
			return arr[0];
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 2; i++) {
				if (i != 0) {
					sb.append(".");
				}
				sb.append(arr[i]);
			}

			return sb.toString();
		}
	}
}
