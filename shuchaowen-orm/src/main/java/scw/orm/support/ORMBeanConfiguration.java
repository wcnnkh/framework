package scw.orm.support;

import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.beans.SimpleBeanConfiguration;
import scw.beans.annotation.Configuration;
import scw.beans.property.ValueWiredManager;
import scw.core.GlobalPropertyFactory;
import scw.core.Init;
import scw.core.utils.StringUtils;
import scw.orm.ORMUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order=Integer.MAX_VALUE)
public class ORMBeanConfiguration extends AbstractBeanConfiguration implements SimpleBeanConfiguration {

	public void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addInit(new OrmProxyRegister(propertyFactory));
	}
	
	public static String getScanAnnotationPackageName() {
		return GlobalPropertyFactory.getInstance().getValue(
				"scw.scan.orm.package", String.class, BeanUtils.getScanAnnotationPackageName());
	}

	private static class OrmProxyRegister implements Init {
		private PropertyFactory propertyFactory;

		public OrmProxyRegister(PropertyFactory propertyFactory) {
			this.propertyFactory = propertyFactory;
		}

		public void init() {
			String ormScanPackageName = propertyFactory.getString("orm.scan");
			if (StringUtils.isNotEmpty(ormScanPackageName)) {
				ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
			} else {
				ORMUtils.registerCglibProxyTableBean(getScanAnnotationPackageName());
			}
		}

	}
}
