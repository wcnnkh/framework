package scw.eureka.server;

import java.util.Collection;
import java.util.LinkedHashSet;

import com.sun.jersey.api.client.filter.ClientFilter;

public class ReplicationClientAdditionalFilters {
	private Collection<ClientFilter> filters;

	public ReplicationClientAdditionalFilters(Collection<ClientFilter> filters) {
		this.filters = new LinkedHashSet<>(filters);
	}

	public Collection<ClientFilter> getFilters() {
		return this.filters;
	}
}
