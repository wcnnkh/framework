package io.basc.framework.ibatis.beans;

import io.basc.framework.beans.BeansException;
import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.support.DefaultBeanDefinition;
import io.basc.framework.ibatis.MybatisUtils;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionFactoryBeanDefinition extends DefaultBeanDefinition {

	public SqlSessionFactoryBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, SqlSessionFactory.class);
	}

	public boolean isInstance() {
		return beanFactory.isInstance(Configuration.class);
	}

	public Object create() throws BeansException {
		Configuration configuration = beanFactory.getInstance(Configuration.class);
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
		return MybatisUtils.proxySqlSessionFactory(sqlSessionFactory);
	}
}