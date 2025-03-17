package run.soeasy.framework.net.client;

import java.util.function.Predicate;

import run.soeasy.framework.net.RequestPattern;

public interface ClientRequestFactoryAdapter extends ClientRequestFactory, Predicate<RequestPattern>{
	
}
