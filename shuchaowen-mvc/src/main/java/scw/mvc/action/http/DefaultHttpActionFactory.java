package scw.mvc.action.http;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.Constants;
import scw.core.instance.InstanceUtils;
import scw.core.utils.ClassUtils;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;
import scw.mvc.action.Action;
import scw.mvc.action.ActionFactory;
import scw.util.value.property.PropertyFactory;

public final class DefaultHttpActionFactory implements ActionFactory {
	private ActionFactory actionFactory;

	public DefaultHttpActionFactory(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		DefaultMultiHttpActionFactory multiHttpActionFactory = new DefaultMultiHttpActionFactory();
		BeanUtils.appendBean(multiHttpActionFactory.getFactoryList(), beanFactory, propertyFactory,
				HttpActionFactory.class, "mvc.http.action.factory");
		multiHttpActionFactory.getFactoryList().addAll(InstanceUtils.getConfigurationList(HttpActionFactory.class, beanFactory));
		multiHttpActionFactory.getFactoryList()
				.add(new HttpParameterActionFactory(MVCUtils.getHttpParameterActionKey(propertyFactory)));
		multiHttpActionFactory.getFactoryList().add(new HttpPathActionFactory());
		multiHttpActionFactory.getFactoryList().add(new HttpRestfulActionFactory());
		multiHttpActionFactory.init(beanFactory, propertyFactory, ClassUtils
				.getClassSet(MVCUtils.getScanAnnotationPackageName(), Constants.SYSTEM_PACKAGE_NAME));
		this.actionFactory = multiHttpActionFactory;
	}

	public Action getAction(Channel channel) {
		return actionFactory.getAction(channel);
	}

}
