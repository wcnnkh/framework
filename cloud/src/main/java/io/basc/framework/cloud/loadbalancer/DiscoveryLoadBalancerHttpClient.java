package io.basc.framework.cloud.loadbalancer;

import io.basc.framework.cloud.ServiceInstance;
import io.basc.framework.http.HttpMethod;
import io.basc.framework.http.HttpResponseEntity;
import io.basc.framework.http.client.ClientHttpRequestCallback;
import io.basc.framework.http.client.ClientHttpRequestFactory;
import io.basc.framework.http.client.ClientHttpResponseExtractor;
import io.basc.framework.http.client.DefaultHttpClient;
import io.basc.framework.http.client.exception.HttpClientException;
import io.basc.framework.net.uri.UriComponentsBuilder;

import java.net.URI;
import java.util.HashSet;

public class DiscoveryLoadBalancerHttpClient extends DefaultHttpClient{
	private DiscoveryLoadBalancer loadbalancer;
	
	public DiscoveryLoadBalancerHttpClient(DiscoveryLoadBalancer loadbalancer) {
		this.loadbalancer = loadbalancer;
	}
	
	@Override
	public <T> HttpResponseEntity<T> execute(URI url, HttpMethod method,
			ClientHttpRequestFactory requestFactory,
			ClientHttpRequestCallback requestCallback,
			ClientHttpResponseExtractor<T> responseExtractor)
			throws HttpClientException {
		String host = url.getHost();
		Server<ServiceInstance> server = loadbalancer.choose(host, null);
		if(server == null){
			return super.execute(url, method, requestFactory, requestCallback,
					responseExtractor);
		}
		
		final HashSet<String> errorSets = new HashSet<String>();
		while(server != null){
			UriComponentsBuilder builder = UriComponentsBuilder.fromUri(url);
			builder = builder.host(server.getService().getHost());
			builder = builder.port(server.getService().getPort());
			try {
				return super.execute(builder.build().toUri(), method, requestFactory, requestCallback,
						responseExtractor);
			} catch (HttpClientException e) {
				errorSets.add(server.getId());
				loadbalancer.stat(server, State.FAILED);
				server = loadbalancer.choose(host, new ServerAccept<ServiceInstance>() {
					
					public boolean accept(Server<ServiceInstance> server) {
						return !errorSets.contains(server.getId());
					}
				});
			}
		}
		return super.execute(url, method, requestFactory, requestCallback,
				responseExtractor);
	}
}
