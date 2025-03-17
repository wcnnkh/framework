package run.soeasy.framework.beans.factory;

import run.soeasy.framework.beans.FatalBeanException;

public class BeanDefinitionStoreException extends FatalBeanException {
	private static final long serialVersionUID = 1L;

	private final String resourceDescription;

	private final String beanName;

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param msg the detail message (used as exception message as-is)
	 */
	public BeanDefinitionStoreException(String msg) {
		super(msg);
		this.resourceDescription = null;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param msg   the detail message (used as exception message as-is)
	 * @param cause the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(String msg, Throwable cause) {
		super(msg, cause);
		this.resourceDescription = null;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param resourceDescription description of the resource that the bean
	 *                            definition came from
	 * @param msg                 the detail message (used as exception message
	 *                            as-is)
	 */
	public BeanDefinitionStoreException(String resourceDescription, String msg) {
		super(msg);
		this.resourceDescription = resourceDescription;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param resourceDescription description of the resource that the bean
	 *                            definition came from
	 * @param msg                 the detail message (used as exception message
	 *                            as-is)
	 * @param cause               the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(String resourceDescription, String msg, Throwable cause) {
		super(msg, cause);
		this.resourceDescription = resourceDescription;
		this.beanName = null;
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param resourceDescription description of the resource that the bean
	 *                            definition came from
	 * @param beanName            the name of the bean
	 * @param msg                 the detail message (appended to an introductory
	 *                            message that indicates the resource and the name
	 *                            of the bean)
	 */
	public BeanDefinitionStoreException(String resourceDescription, String beanName, String msg) {
		this(resourceDescription, beanName, msg, null);
	}

	/**
	 * Create a new BeanDefinitionStoreException.
	 * 
	 * @param resourceDescription description of the resource that the bean
	 *                            definition came from
	 * @param beanName            the name of the bean
	 * @param msg                 the detail message (appended to an introductory
	 *                            message that indicates the resource and the name
	 *                            of the bean)
	 * @param cause               the root cause (may be {@code null})
	 */
	public BeanDefinitionStoreException(String resourceDescription, String beanName, String msg, Throwable cause) {

		super("Invalid bean definition with name '" + beanName + "' defined in " + resourceDescription + ": " + msg,
				cause);
		this.resourceDescription = resourceDescription;
		this.beanName = beanName;
	}

	/**
	 * Return the description of the resource that the bean definition came from, if
	 * available.
	 */
	public String getResourceDescription() {
		return this.resourceDescription;
	}

	/**
	 * Return the name of the bean, if available.
	 */
	public String getBeanName() {
		return this.beanName;
	}
}
