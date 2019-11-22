package scw.mvc.support;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.utils.ClassUtils;
import scw.mvc.Action;
import scw.mvc.Channel;
import scw.mvc.MVCUtils;

public final class DefaultHttpActionFactory implements ActionFactory {
	private ActionFactory actionFactory;

	public DefaultHttpActionFactory(BeanFactory beanFactory, PropertyFactory propertyFactory) {
		DefaultMultiHttpActionFactory multiHttpActionFactory = new DefaultMultiHttpActionFactory(beanFactory);
		BeanUtils.appendBean(multiHttpActionFactory.getFactoryList(), beanFactory, propertyFactory,
				HttpActionFactory.class, "mvc.http.action.factory", true);
		multiHttpActionFactory.getFactoryList()
				.add(new HttpParameterActionFactory(MVCUtils.getHttpParameterActionKey(propertyFactory)));
		multiHttpActionFactory.getFactoryList().add(new HttpPathActionFactory());
		multiHttpActionFactory.getFactoryList().add(new HttpRestfulActionFactory());
		multiHttpActionFactory.init(beanFactory, propertyFactory,
				ClassUtils.getClassList(BeanUtils.getPackageName(propertyFactory, "mvc.http.scanning")));
		this.actionFactory = multiHttpActionFactory;
	}

	public Action getAction(Channel channel) {
		return actionFactory.getAction(channel);
	}

}
