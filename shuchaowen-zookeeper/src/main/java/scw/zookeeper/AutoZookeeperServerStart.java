package scw.zookeeper;

import scw.beans.BeanFactory;
import scw.beans.configuration.BeanFactoryLifeCycle;
import scw.core.instance.annotation.Configuration;
import scw.io.resource.ResourceUtils;
import scw.util.value.property.PropertyFactory;

@Configuration(order=Integer.MAX_VALUE)
public final class AutoZookeeperServerStart implements BeanFactoryLifeCycle{
	private static final String DEFAULT_ZOOKEEPER_CONFIG = "zookeeper.properties";
	
	public void init(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		ZookeeperServerStart start = null;
		if(ResourceUtils.getResourceOperations().isExist(DEFAULT_ZOOKEEPER_CONFIG)){
			start = new ZookeeperServerStart(ResourceUtils.getResourceOperations().getProperties(DEFAULT_ZOOKEEPER_CONFIG));
		}else{
			Integer port = propertyFactory.getInteger("zookeeper.port");
			if(port != null){
				start = new ZookeeperServerStart(port);
			}
		}
		
		if(start != null){
			start.start();
		}
	}

	public void destroy(BeanFactory beanFactory, PropertyFactory propertyFactory)
			throws Exception {
		// TODO Auto-generated method stub
		
	}

}
