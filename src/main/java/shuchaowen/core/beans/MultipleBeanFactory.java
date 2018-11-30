package shuchaowen.core.beans;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.core.exception.BeansException;
import shuchaowen.core.util.Logger;

public final class MultipleBeanFactory extends AbstractBeanFactory {
	private List<BeanFactory> beanFactoryList;
	private volatile boolean firstGet = false;

	public MultipleBeanFactory() {
		singletonMap.put(this.getClass().getName(), this);
		nameMappingMap.put(BeanFactory.class.getName(), this.getClass()
				.getName());
	}

	public void addLastBeanFactory(BeanFactory beanFactory) {
		if(firstGet){
			Logger.warn(this.getClass().getName(), "你现在向其中添加一个新的实体工厂，但这个实例工厂已经被使用过了(已调用过get方法), factory=" + beanFactory.getClass().getName());
		}
		
		if (beanFactoryList == null) {
			beanFactoryList = new ArrayList<BeanFactory>();
		}
		beanFactoryList.add(beanFactory);
	}
	
	public void addFirst(BeanFactory beanFactory){
		if(firstGet){
			Logger.warn(this.getClass().getName(), "你现在向其中添加一个新的实体工厂，但这个实例工厂已经被使用过了(已调用过get方法), factory=" + beanFactory.getClass().getName());
		}
		
		if (beanFactoryList == null) {
			beanFactoryList = new ArrayList<BeanFactory>();
		}
		beanFactoryList.add(0, beanFactory);
	}
	
	public <T> T get(String name) {
		if(!firstGet){
			firstGet = true;
		}
		
		T t = super.get(name);
		if (t == null && beanFactoryList != null) {
			for (BeanFactory beanFactory : beanFactoryList) {
				t = beanFactory.get(name);
				if (t == null) {
					continue;
				}
			}
		}

		if (t == null) {
			throw new BeansException(name + " not found");
		}
		return t;
	}

	public <T> T get(Class<T> type) {
		if(!firstGet){
			firstGet = true;
		}
		
		T t = super.get(type);
		if (t == null && beanFactoryList != null) {
			for (BeanFactory beanFactory : beanFactoryList) {
				t = beanFactory.get(type);
				if (t == null) {
					continue;
				}
			}
		}
		if (t == null) {
			throw new BeansException(type.getName() + " not found");
		}
		return t;
	}

	public Bean getBean(String name) {
		if (beanFactoryList == null) {
			throw new BeansException(name + " not found");
		}

		for (BeanFactory beanFactory : beanFactoryList) {
			Bean bean = beanFactory.getBean(name);
			if (bean == null) {
				continue;
			}
			return bean;
		}
		throw new BeansException(name + " not found");
	}

	public boolean contains(String name) {
		if (super.contains(name)) {
			return true;
		}

		if (beanFactoryList != null) {
			for (BeanFactory beanFactory : beanFactoryList) {
				if (beanFactory.contains(name)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void destroy() {
		super.destroy();
		if (beanFactoryList != null) {
			for (BeanFactory beanFactory : beanFactoryList) {
				if (beanFactory instanceof AbstractBeanFactory) {
					((AbstractBeanFactory) beanFactory).destroy();
				}
			}
		}
	}

	@Override
	protected Bean newBean(String name) {
		return null;
	}
}
