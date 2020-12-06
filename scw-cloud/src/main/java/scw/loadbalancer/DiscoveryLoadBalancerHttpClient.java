package scw.loadbalancer;

import java.net.URI;
import java.util.HashSet;

import scw.discovery.ServiceInstance;
import scw.http.HttpMethod;
import scw.http.HttpResponseEntity;
import scw.http.client.ClientHttpRequestCallback;
import scw.http.client.ClientHttpRequestFactory;
import scw.http.client.ClientHttpResponseExtractor;
import scw.http.client.DefaultHttpClient;
import scw.http.client.exception.HttpClientException;

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
			try {
				return super.execute(url, method, requestFactory, requestCallback,
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
