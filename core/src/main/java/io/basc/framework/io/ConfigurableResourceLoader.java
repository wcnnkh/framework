package io.basc.framework.io;

public interface ConfigurableResourceLoader extends ResourceLoader {

	ConfigurableProtocolResolver getProtocolResolver();
}
