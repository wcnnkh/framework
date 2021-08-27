package io.basc.framework.aop.support;

import io.basc.framework.aop.MethodInterceptor;
import io.basc.framework.core.utils.CollectionUtils;
import io.basc.framework.instance.Configurable;
import io.basc.framework.instance.ServiceLoaderFactory;
import io.basc.framework.util.MultiIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ConfigurableMethodInterceptor extends AbstractMethodInterceptors implements Configurable {
	private static final long serialVersionUID = 1L;
	private volatile List<MethodInterceptor> defaultInterceptors;
	private volatile List<MethodInterceptor> interceptors;

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		this.defaultInterceptors = serviceLoaderFactory.getServiceLoader(MethodInterceptor.class).toList();
	}

	public Iterator<MethodInterceptor> iterator() {
		if (interceptors == null && defaultInterceptors == null) {
			return Collections.emptyIterator();
		}

		// 不直接使用[interceptors.iterator()]的目的是为了降低方法调用的嵌套层级
		List<Iterator<MethodInterceptor>> iterators = new ArrayList<Iterator<MethodInterceptor>>(size());
		if (interceptors != null) {
			for (MethodInterceptor interceptor : interceptors) {
				if (interceptor == null) {
					continue;
				}

				if (interceptor instanceof AbstractMethodInterceptors) {
					iterators.add(((AbstractMethodInterceptors) interceptor).iterator());
				} else {
					iterators.add(Arrays.asList(interceptor).iterator());
				}
			}
		}

		if (defaultInterceptors != null) {
			for (MethodInterceptor interceptor : defaultInterceptors) {
				if (interceptor == null) {
					continue;
				}

				if (interceptor instanceof AbstractMethodInterceptors) {
					iterators.add(((AbstractMethodInterceptors) interceptor).iterator());
				} else {
					iterators.add(Arrays.asList(interceptor).iterator());
				}
			}
		}
		return new MultiIterator<MethodInterceptor>(iterators);
	}

	public int size() {
		return CollectionUtils.size(interceptors) + CollectionUtils.size(defaultInterceptors);
	}

	public boolean isEmpty() {
		return size() == 0;
	}

	private void init() {
		if (interceptors == null) {
			synchronized (this) {
				if (interceptors == null) {
					interceptors = new CopyOnWriteArrayList<MethodInterceptor>();
				}
			}
		}
	}

	public void addMethodInterceptor(MethodInterceptor methodInterceptor) {
		init();
		this.interceptors.add(methodInterceptor);
	}

	public void addFirstMethodInterceptor(MethodInterceptor methodInterceptor) {
		init();
		this.interceptors.add(0, methodInterceptor);
	}
}
