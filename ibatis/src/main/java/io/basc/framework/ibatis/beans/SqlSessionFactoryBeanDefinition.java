package io.basc.framework.ibatis.beans;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.factory.BeanFactory;
import io.basc.framework.beans.factory.support.FactoryBeanDefinition;
import io.basc.framework.ibatis.MybatisUtils;

public class SqlSessionFactoryBeanDefinition extends FactoryBeanDefinition {

	public SqlSessionFactoryBeanDefinition(BeanFactory beanFactory) {
		super(beanFactory, SqlSessionFactory.class);
	}

	public boolean isInstance() {
		return getBeanFactory().isInstance(Configuration.class);
	}

	public Object create() throws BeansException {
		Configuration configuration = getBeanFactory().getInstance(Configuration.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		return MybatisUtils.proxySqlSessionFactory(sqlSessionFactory);
	}
}