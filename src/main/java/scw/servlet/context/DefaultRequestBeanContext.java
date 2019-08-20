package scw.servlet.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import scw.core.exception.BeansException;
import scw.servlet.Request;
import scw.servlet.beans.RequestBean;
import scw.servlet.beans.RequestBeanFactory;

public class DefaultRequestBeanContext implements RequestBeanContext {
	private volatile LinkedHashMap<String, Object> beanMap;
	private final Request request;
	private final RequestBeanFactory requestBeanFactory;

	public DefaultRequestBeanContext(Request request, RequestBeanFactory requestBeanFactory) {
		this.request = request;
		this.requestBeanFactory = requestBeanFactory;
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(Class<T> type) {
		RequestBean requestBean = requestBeanFactory.get(type.getName());
		if (requestBean == null) {
			return null;
		}
		return (T) getBean(requestBean, type.getName());
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		RequestBean requestBean = requestBeanFactory.get(name);
		if (requestBean == null) {
			return null;
		}

		return (T) getBean(requestBean, name);
	}

	private Object getBean(RequestBean requestBean, String name) {
		Object obj = null;
		if (beanMap == null) {
			synchronized (this) {
				if (beanMap == null) {
					beanMap = new LinkedHashMap<String, Object>(4);
					obj = newInstanceReuestBean(requestBean);
				}
			}
		} else {
			obj = beanMap.get(requestBean.getId());
			if (obj == null) {
				synchronized (this) {
					obj = beanMap.get(requestBean.getId());
					if (obj == null) {
						obj = newInstanceReuestBean(requestBean);
					}
				}
			}
		}
		return obj;
	}

	private Object newInstanceReuestBean(RequestBean requestBean) {
		Object obj = requestBean.newInstance(request);
		if (obj != null) {
			beanMap.put(requestBean.getId(), obj);
			try {
				requestBean.autowrite(obj);
				requestBean.init(obj);
			} catch (Exception e) {
				throw new BeansException(e);
			}
		}
		return obj;
	}

	public void destroy() {
		if (beanMap != null) {
			synchronized (this) {
				if (beanMap != null) {
					List<String> beanKeyList = new ArrayList<String>();
					for (Entry<String, Object> entry : beanMap.entrySet()) {
						beanKeyList.add(entry.getKey());
					}

					for (String id : beanKeyList) {
						RequestBean requestBean = requestBeanFactory.get(id);
						try {
							requestBean.destroy(beanMap.get(id));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				beanMap = null;
			}
		}
	}

	public final Request getRequest() {
		return request;
	}

	public final RequestBeanFactory getRequestBeanFactory() {
		return requestBeanFactory;
	}
}
