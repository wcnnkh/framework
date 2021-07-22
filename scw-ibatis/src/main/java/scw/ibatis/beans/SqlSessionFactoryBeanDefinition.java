package scw.ibatis.beans;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.ibatis.MybatisUtils;

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