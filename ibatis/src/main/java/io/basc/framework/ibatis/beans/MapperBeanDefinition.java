package io.basc.framework.ibatis.beans;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.factory.InstanceException;
import io.basc.framework.ibatis.MybatisUtils;

import org.apache.ibatis.session.SqlSessionFactory;

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
