package io.basc.framework.context;

import io.basc.framework.beans.factory.HierarchicalBeanFactory;
import io.basc.framework.beans.factory.ListableBeanFactory;
import io.basc.framework.beans.factory.config.AutowireCapableBeanFactory;
import io.basc.framework.env.EnvironmentCapable;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.ClassLoaderProvider;
import io.basc.framework.util.Elements;
import io.basc.framework.util.ParentDiscover;
import io.basc.framework.util.observe_old.Observable;

public interface ApplicationContext
		extends EnvironmentCapable, ClassLoaderProvider, ParentDiscover<ApplicationContext>, ResourcePatternResolver,
		ListableBeanFactory, HierarchicalBeanFactory, Observable<ApplicationContextEvent>, AutowireCapableBeanFactory {

	/**
	 * Return the unique id of this application context.
	 * 
	 * @return the unique id of the context, or {@code null} if none
	 */
	/*
	 * @Nullable String getId();
	 * 
	 *//**
		 * Return a name for the deployed application that this context belongs to.
		 * 
		 * @return a name for the deployed application, or the empty String by default
		 */
	/*
	 * String getApplicationName();
	 * 
	 *//**
		 * Return a friendly name for this context.
		 * 
		 * @return a display name for this context (never {@code null})
		 *//*
			 * String getDisplayName();
			 */

	/**
	 * Return the parent context, or {@code null} if there is no parent and this is
	 * the root of the context hierarchy.
	 * 
	 * @return the parent context, or {@code null} if there is no parent
	 */
	@Nullable
	ApplicationContext getParent();

	AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException;

	default Elements<Resource> getProfileResources(String location) {
		Resource rootResource = getResource(location);
		Elements<Resource> root = Elements.singleton(rootResource);
		Elements<String> profiles = getEnvironment().getProfiles(location);
		if (profiles.isEmpty()) {
			return root;
		}

		Elements<Resource> resourceProfiles = profiles.map((name) -> getResource(name));
		return root.concat(resourceProfiles);
	}
}