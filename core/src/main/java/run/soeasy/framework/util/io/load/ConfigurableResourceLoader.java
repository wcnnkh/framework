package run.soeasy.framework.util.io.load;

public interface ConfigurableResourceLoader extends ResourceLoader {

	ConfigurableProtocolResolver getProtocolResolver();
}
