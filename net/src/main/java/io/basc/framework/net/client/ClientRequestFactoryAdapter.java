package io.basc.framework.net.client;

import java.util.function.Predicate;

import io.basc.framework.net.RequestPattern;

public interface ClientRequestFactoryAdapter extends ClientRequestFactory, Predicate<RequestPattern>{
	
}
