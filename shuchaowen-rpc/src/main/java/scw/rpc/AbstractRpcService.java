package scw.rpc;

import scw.aop.Invoker;
import scw.lang.NotFoundException;

public abstract class AbstractRpcService implements RpcService {

	public ResponseMessage service(RequestMessage requestMessage) {
		Object value = null;
		Throwable error = null;
		try {
			value = serviceInternal(requestMessage);
		} catch (Throwable e) {
			error = e;
		}

		return createResponseMessage(requestMessage, value, error);
	}

	private Object serviceInternal(RequestMessage requestMessage) throws Throwable {
		if (requestMessage == null) {
			throw new NullPointerException("requestMessage is null");
		}

		if (requestMessage.getMethod() == null) {
			throw new NotFoundException(requestMessage.toString());
		}

		Invoker invoker = getInvoker(requestMessage);
		return invoker.invoke(requestMessage.getParameters());
	}

	protected abstract Invoker getInvoker(RequestMessage requestMessage) throws NotFoundException;

	protected abstract ResponseMessage createResponseMessage(RequestMessage requestMessage, Object value,
			Throwable error);
}
