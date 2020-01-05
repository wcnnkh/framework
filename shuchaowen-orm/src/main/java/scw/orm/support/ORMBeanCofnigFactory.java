package scw.orm.support;

import scw.application.ApplicationConfigUtils;
import scw.beans.AbstractBeanConfiguration;
import scw.beans.BeanFactory;
import scw.beans.SimpleBeanConfiguration;
import scw.beans.annotation.Configuration;
import scw.beans.property.ValueWiredManager;
import scw.core.Init;
import scw.core.PropertyFactory;
import scw.core.utils.StringUtils;
import scw.orm.ORMUtils;

@Configuration(order=Integer.MAX_VALUE)
public class ORMBeanCofnigFactory extends AbstractBeanConfiguration implements SimpleBeanConfiguration {

	public void init(ValueWiredManager valueWiredManager, BeanFactory beanFactory, PropertyFactory propertyFactory) {
		addInit(new OrmProxyRegister(propertyFactory));
	}

	private static class OrmProxyRegister implements Init {
		private PropertyFactory propertyFactory;

		public OrmProxyRegister(PropertyFactory propertyFactory) {
			this.propertyFactory = propertyFactory;
		}

		public void init() {
			String ormScanPackageName = propertyFactory.getProperty("orm.scan");
			if (StringUtils.isNotEmpty(ormScanPackageName)) {
				ORMUtils.registerCglibProxyTableBean(ormScanPackageName);
			} else {
				ORMUtils.registerCglibProxyTableBean(ApplicationConfigUtils.getORMPackage(propertyFactory));
			}
		}

	}
}
