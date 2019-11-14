package scw.mvc.support;

import scw.beans.BeanFactory;
import scw.beans.BeanUtils;
import scw.core.PropertyFactory;
import scw.core.resource.ResourceUtils;
import scw.core.utils.StringUtils;
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
		String packageName = propertyFactory.getProperty("mvc.http.scanning");
		packageName = StringUtils.isEmpty(packageName) ? "" : packageName;
		multiHttpActionFactory.init(beanFactory, propertyFactory, ResourceUtils.getClassList(packageName));
		this.actionFactory = multiHttpActionFactory;
	}

	public Action getAction(Channel channel) {
		return actionFactory.getAction(channel);
	}

}
