package scw.loadbalancer;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import scw.boot.ApplicationUtils;
import scw.core.instance.annotation.SPI;
import scw.discovery.DiscoveryClient;
import scw.discovery.ServiceInstance;
import scw.value.property.PropertyFactory;

@SPI(order=Integer.MIN_VALUE, value=DiscoveryLoadBalancer.class, assignableValue=false)
public class RoundRobinDiscoveryLoadBalancer implements DiscoveryLoadBalancer{
	private DiscoveryClient discoveryClient;
	private String name;
	private ConcurrentHashMap<String, LoadBalancer<ServiceInstance>> loadBalancerMap = new ConcurrentHashMap<String, LoadBalancer<ServiceInstance>>();
	
	public RoundRobinDiscoveryLoadBalancer(DiscoveryClient discoveryClient, PropertyFactory propertyFactory) {
		this(discoveryClient, ApplicationUtils.getApplicatoinName(propertyFactory));
	}
	
	public RoundRobinDiscoveryLoadBalancer(DiscoveryClient discoveryClient, String name) {
		this.discoveryClient = discoveryClient;
		this.name = name;
	}
	
	private LoadBalancer<ServiceInstance> getLoadBalancer(String name){
		LoadBalancer<ServiceInstance> loadBalancer = loadBalancerMap.get(name);
		if(loadBalancer == null){
			loadBalancer = new RoundRobinLoadBalancer<ServiceInstance>(new DiscoverySupplier(discoveryClient, name));
			LoadBalancer<ServiceInstance> old = loadBalancerMap.putIfAbsent(name, loadBalancer);
			if(old != null){
				loadBalancer = old;
			}
		}
		return loadBalancer;
	}
	
	public Server<ServiceInstance> choose(ServerAccept<ServiceInstance> accept) {
		return getLoadBalancer(name).choose(accept);
	}

	public Server<ServiceInstance> choose(String name,
			ServerAccept<ServiceInstance> accept) {
		return getLoadBalancer(name).choose(accept);
	}
	
	public void stat(Server<ServiceInstance> server, State state) {
		for(Entry<String, LoadBalancer<ServiceInstance>> entry : loadBalancerMap.entrySet()){
			entry.getValue().stat(server, state);
		}
	}
}
