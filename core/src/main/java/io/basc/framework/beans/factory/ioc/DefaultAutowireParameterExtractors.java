package io.basc.framework.beans.factory.ioc;

public class DefaultAutowireParameterExtractors extends AutowireParameterExtractors {
	public DefaultAutowireParameterExtractors() {
		setLastService(defaults());
	}
}
