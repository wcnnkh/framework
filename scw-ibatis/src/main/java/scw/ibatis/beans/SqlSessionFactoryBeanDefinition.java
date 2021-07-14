package scw.ibatis.beans;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import scw.beans.BeansException;
import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.ibatis.MybatisUtils;
import scw.io.Resource;
import scw.io.ResourceUtils;

public class SqlSessionFactoryBeanDefinition extends DefaultBeanDefinition {

	public SqlSessionFactoryBeanDefinition(ConfigurableBeanFactory beanFactory) {
		super(beanFactory, SqlSessionFactory.class);
	}

	public boolean isInstance() {
		return true;
	}

	public Object create() throws BeansException {
		Resource resource = beanFactory.getEnvironment().getResource("mybatis-config.xml");
		SqlSessionFactory sqlSessionFactory;
		if (resource != null && resource.exists()) {
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(ResourceUtils.getInputStream(resource));
		} else {
			sqlSessionFactory = new SqlSessionFactoryBuilder()
					.build(beanFactory.getInstance(org.apache.ibatis.session.Configuration.class));
		}
		return MybatisUtils.proxySqlSessionFactory(sqlSessionFactory);
	}
}