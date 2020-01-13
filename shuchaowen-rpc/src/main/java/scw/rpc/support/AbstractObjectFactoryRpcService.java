package scw.rpc.support;

import scw.aop.Invoker;
import scw.aop.ReflectInvoker;
import scw.core.instance.InstanceFactory;
import scw.lang.NotFoundException;
import scw.rpc.AbstractRpcService;
import scw.rpc.RequestMessage;

public abstract class AbstractObjectFactoryRpcService extends AbstractRpcService {
	private InstanceFactory instanceFactory;

	public AbstractObjectFactoryRpcService(InstanceFactory instanceFactory) {
		this.instanceFactory = instanceFactory;
	}

	@Override
	protected Invoker getInvoker(RequestMessage requestMessage) throws NotFoundException {
		Object bean = instanceFactory.getInstance(requestMessage.getSourceClass());
		if (bean == null) {
			throw new NotFoundException(requestMessage.toString());
		}

		return new ReflectInvoker(bean, requestMessage.getMethod());
	}
}
