package io.basc.framework.util.io.load;

public interface ConfigurableResourceLoader extends ResourceLoader {

	ConfigurableProtocolResolver getProtocolResolver();
}
