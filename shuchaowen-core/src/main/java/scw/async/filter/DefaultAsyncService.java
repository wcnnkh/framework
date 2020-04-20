package scw.async.filter;

import scw.async.AsyncExecutor;
import scw.core.instance.InstanceFactory;

public class DefaultAsyncService extends AbstractAsyncService{
	
	public DefaultAsyncService(InstanceFactory instanceFactory) {
		super(instanceFactory);
	}

	@Override
	protected AsyncExecutor getAsyncExecutor() {
		// TODO Auto-generated method stub
		return null;
	}
}
