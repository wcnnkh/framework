package scw.loadbalancer;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import scw.core.utils.XTime;

public class TimerServerSupplier<T> extends TimerTask implements ServerSupplier<T>{
	private static final Timer TIMER = new Timer(TimerServerSupplier.class.getName(), true);
	private ServerSupplier<T> serverSupplier;
	private volatile List<Server<T>> servers;
	private long period;
	private ConcurrentHashMap<String, Long> failMap = new ConcurrentHashMap<String, Long>();
	private AtomicBoolean timerTag = new AtomicBoolean(false);
	
	public TimerServerSupplier(ServerSupplier<T> serverSupplier){
		this(serverSupplier, XTime.ONE_MINUTE);
	}
	
	public TimerServerSupplier(ServerSupplier<T> serverSupplier, long period){
		this.serverSupplier = serverSupplier;
		this.period = period;
		this.servers = getBasicServers();
	}
	
	public ServerSupplier<T> getServerSupplier() {
		return serverSupplier;
	}

	public List<Server<T>> getBasicServers(){
		List<Server<T>> list = new ArrayList<Server<T>>();
		for (Server<T> server : serverSupplier.getServers()) {
			if (server == null) {
				continue;
			}
			
			if(failMap.containsKey(server.getId())){
				continue;
			}

			int weight = server.getWeight();
			if (weight < 0) {
				continue;
			}
			list.add(server);
		}
		return list;
	}
	
	public List<Server<T>> getServers() {
		if(!timerTag.get() && timerTag.compareAndSet(false, true)){
			TIMER.schedule(this, period, period);
		}
		
		if(servers == null){
			return getBasicServers();
		}
		return servers;
	}

	@Override
	public void run() {
		this.servers = getServers();
		for(String id : failMap.keySet()){
			if((System.currentTimeMillis() - failMap.get(id)) > (period * 3)){
				failMap.remove(id);
			}
		}
	}
	
	public void stat(Server<T> server, State state){
		if(state == State.FAILED){
			failMap.put(server.getId(), System.currentTimeMillis());
		}
	}
}
