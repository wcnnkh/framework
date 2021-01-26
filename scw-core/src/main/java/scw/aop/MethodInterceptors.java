package scw.aop;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import scw.core.utils.ArrayUtils;
import scw.core.utils.CollectionUtils;
import scw.instance.NoArgsInstanceFactory;
import scw.instance.support.InstanceIterable;
import scw.util.MultiIterable;

public class MethodInterceptors implements Serializable, Iterable<MethodInterceptor> {
	private static final long serialVersionUID = 1L;
	private LinkedList<Iterable<? extends MethodInterceptor>> iterables = new LinkedList<Iterable<? extends MethodInterceptor>>();

	public void addFirst(Iterable<? extends MethodInterceptor> iterable) {
		this.iterables.addFirst(iterable);
	}

	public void addFirst(MethodInterceptor... methodInterceptors) {
		if (ArrayUtils.isEmpty(methodInterceptors)) {
			return;
		}

		this.iterables.addFirst(Arrays.asList(methodInterceptors));
	}

	public void addFirst(NoArgsInstanceFactory instanceFactory, Collection<String> filterNames) {
		if (CollectionUtils.isEmpty(filterNames)) {
			return;
		}

		addFirst(new InstanceIterable<MethodInterceptor>(instanceFactory, filterNames));
	}

	public void addLast(Iterable<? extends MethodInterceptor> iterable) {
		this.iterables.addLast(iterable);
	}

	public void addLast(MethodInterceptor... methodInterceptors) {
		if (ArrayUtils.isEmpty(methodInterceptors)) {
			return;
		}

		this.iterables.addLast(Arrays.asList(methodInterceptors));
	}

	public void addLast(NoArgsInstanceFactory instanceFactory, Collection<String> filterNames) {
		if (CollectionUtils.isEmpty(filterNames)) {
			return;
		}

		addLast(new InstanceIterable<MethodInterceptor>(instanceFactory, filterNames));
	}

	public Iterator<MethodInterceptor> iterator() {
		return new MultiIterable<MethodInterceptor>(iterables).iterator();
	}
}
