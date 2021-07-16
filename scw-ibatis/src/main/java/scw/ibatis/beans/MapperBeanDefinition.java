package scw.ibatis.beans;

import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.ibatis.MybatisUtils;
import scw.instance.InstanceException;

public class MapperBeanDefinition extends DefaultBeanDefinition {

	public MapperBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return beanFactory.isInstance(SqlSessionFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		SqlSessionFactory sqlSessionFactory = beanFactory.getInstance(SqlSessionFactory.class);
		return MybatisUtils.proxyMapper(getTargetClass(), (p) -> p.create(), (m) -> sqlSessionFactory.openSession());
	}
}
