package shuchaowen.core.beans;

import java.util.ArrayList;
import java.util.List;

import shuchaowen.core.beans.exception.BeansException;

public final class MultipleBeanFactory extends AbstractBeanFactory {
	private List<BeanFactory> beanFactoryList;

	public MultipleBeanFactory() {
		registerSingleton(this.getClass(), this);
		registerSingleton(BeanFactory.class, this);
	}

	public void addBeanFactory(BeanFactory beanFactory) {
		if (beanFactoryList == null) {
			beanFactoryList = new ArrayList<BeanFactory>();
		}
		beanFactoryList.add(beanFactory);
	}

	public <T> T get(String name) {
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
				if(beanFactory instanceof AbstractBeanFactory){
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
