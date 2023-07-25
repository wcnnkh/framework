package io.basc.framework.mvc;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

import io.basc.framework.mvc.action.Action;
import io.basc.framework.security.authority.http.HttpAuthority;
import io.basc.framework.security.authority.http.HttpAuthorityManager;
import io.basc.framework.util.Assert;
import io.basc.framework.util.registry.Registration;

public class ActionResolverChain extends ActionResolverConfiguration implements ActionResolver {
	private final Iterator<? extends ActionResolverExtend> iterator;
	private final ActionResolver nextChain;

	public ActionResolverChain(Iterator<? extends ActionResolverExtend> iterator) {
		this(iterator, null);
	}

	ActionResolverChain(Iterator<? extends ActionResolverExtend> iterator, ActionResolver nextChain) {
		Assert.requiredArgument(iterator != null, "iterator");
		this.iterator = iterator;
		this.nextChain = nextChain;
	}

	@Override
	public String getControllerId(Class<?> clazz, Method method) {
		if (iterator.hasNext()) {
			return iterator.next().getControllerId(clazz, method, this);
		}
		return nextChain == null ? super.getControllerId(clazz, method) : nextChain.getControllerId(clazz, method);
	}

	@Override
	public Collection<String> getActionInterceptorNames(Class<?> sourceClass, Method method) {
		if (iterator.hasNext()) {
			return iterator.next().getActionInterceptorNames(sourceClass, method, this);
		}
		return nextChain == null ? super.getActionInterceptorNames(sourceClass, method)
				: nextChain.getActionInterceptorNames(sourceClass, method);
	}

	@Override
	public Registration registerHttpAuthority(HttpAuthorityManager<? super HttpAuthority> httpAuthorityManager,
			Action action) {
		if (iterator.hasNext()) {
			return iterator.next().registerHttpAuthority(httpAuthorityManager, action, this);
		}

		if (nextChain == null) {
			return super.registerHttpAuthority(httpAuthorityManager, action);
		} else {
			return nextChain.registerHttpAuthority(httpAuthorityManager, action);
		}
	}

	public static ActionResolverChain build(Iterator<? extends ActionResolverExtend> iterator) {
		return new ActionResolverChain(iterator);
	}

	public static ActionResolverChain build(Iterator<? extends ActionResolverExtend> iterator,
			ActionResolver nextChain) {
		return new ActionResolverChain(iterator, nextChain);
	}

}
