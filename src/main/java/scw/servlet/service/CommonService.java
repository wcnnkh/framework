package scw.servlet.service;

import java.util.Collection;
import java.util.LinkedList;

import scw.beans.BeanFactory;
import scw.beans.annotation.Autowrite;
import scw.beans.annotation.Bean;
import scw.beans.annotation.InitMethod;
import scw.common.utils.ClassUtils;
import scw.servlet.Request;
import scw.servlet.Response;

@Bean(proxy=false)
public class CommonService implements Service {
	@Autowrite
	private BeanFactory beanFactory;
	private LinkedList<Service> services = new LinkedList<Service>();
	private String packageName;
	private String parameterActionKey;

	public CommonService(String packageName, String parameterActionKey) {
		this.packageName = packageName;
		this.parameterActionKey = parameterActionKey;
	}

	public synchronized void addService(Service service) {
		services.add(service);
	}

	@InitMethod
	public void init() {
		Collection<Class<?>> classes = ClassUtils.getClasses(packageName);
		Service servletPathService = beanFactory.get(ServletPathService.class, beanFactory, classes);
		Service parameterActionService = beanFactory.get(ParameterActionService.class, beanFactory, classes,
				parameterActionKey);
		Service restService = beanFactory.get(RestService.class, beanFactory, classes);
		Service notFoundService = beanFactory.get(NotFoundService.class);

		addService(servletPathService);
		addService(parameterActionService);
		addService(restService);
		addService(notFoundService);
	}

	public void service(Request request, Response response, ServiceChain serviceChain) throws Throwable {
		DefaultServiceChain defaultServiceChain = new DefaultServiceChain(services);
		defaultServiceChain.service(request, response);
		serviceChain.service(request, response);
	}
}
