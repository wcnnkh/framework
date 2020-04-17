package scw.orm.support;

import scw.beans.BeanFactory;
import scw.beans.BeanFactoryLifeCycle;
import scw.beans.BeanUtils;
import scw.core.GlobalPropertyFactory;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;
import scw.orm.ORMUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order=Integer.MAX_VALUE)
public final class ORMBeanScann implements BeanFactoryLifeCycle {

	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.orm.package", String.class, BeanUtils.getScanAnnotationPackageName());
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
	}

	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		String ormScanPackageName = propertyFactory.getString("orm.scan");
		if (StringUtils.isNotEmpty(ormScanPackageName)) {
			ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
		} else {
			ORMUtils.registerCglibProxyTableBean(getScanAnnotationPackageName());
		}
	}
}
