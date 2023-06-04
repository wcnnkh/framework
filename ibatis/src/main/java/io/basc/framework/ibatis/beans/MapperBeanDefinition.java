package io.basc.framework.ibatis.beans;

import org.apache.ibatis.session.SqlSessionFactory;

import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.InstanceException;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.ibatis.MybatisUtils;

public class MapperBeanDefinition extends FactoryBeanDefinition {

	public MapperBeanDefinition(BeanFactory beanFactory, Class<?> sourceClass) {
		super(beanFactory, sourceClass);
	}

	@Override
	public boolean isInstance() {
		return getBeanFactory().isInstance(SqlSessionFactory.class);
	}

	@Override
	public Object create() throws InstanceException {
		SqlSessionFactory sqlSessionFactory = getBeanFactory().getInstance(SqlSessionFactory.class);
		return MybatisUtils.proxyMapper(getTypeDescriptor().getType(), (p) -> p.create(),
				(m) -> sqlSessionFactory.openSession());
	}
}
