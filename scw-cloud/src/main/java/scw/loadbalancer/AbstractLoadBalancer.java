package scw.loadbalancer;

import scw.logger.Logger;
import scw.logger.LoggerFactory;

public abstract class AbstractLoadBalancer<T> implements LoadBalancer<T>{
	private static Logger logger = LoggerFactory.getLogger(LoadBalancer.class);
	private final TimerServerSupplier<T> serverSupplier;
	
	public AbstractLoadBalancer(ServerSupplier<T> serverSupplier){
		this.serverSupplier = new TimerServerSupplier<T>(serverSupplier);
	}
	
	public ServerSupplier<T> getServerSupplier() {
		return serverSupplier;
	}

	public void stat(Server<T> server, State state) {
		logger.debug("Stat server [{}] state [{}]", server, state);
		serverSupplier.stat(server, state);
	}
}
