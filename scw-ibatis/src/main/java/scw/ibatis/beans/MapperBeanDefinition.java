package scw.ibatis.beans;

import org.apache.ibatis.session.SqlSessionFactory;

import scw.beans.ConfigurableBeanFactory;
import scw.beans.support.DefaultBeanDefinition;
import scw.ibatis.MybatisUtils;
import scw.instance.InstanceException;

public class MapperBeanDefinition extends DefaultBeanDefinition {
	private final SqlSessionFactory sqlSessionFactory;

	public MapperBeanDefinition(ConfigurableBeanFactory beanFactory, Class<?> sourceClass,
			SqlSessionFactory sqlSessionFactory) {
		super(beanFactory, sourceClass);
		this.sqlSessionFactory = sqlSessionFactory;
	}

	@Override
	public boolean isInstance() {
		return true;
	}

	@Override
	public Object create() throws InstanceException {
		return MybatisUtils.proxyMapper(sqlSessionFactory, getTargetClass(), (p) -> p.create(), (m) -> sqlSessionFactory.openSession());
	}
}
