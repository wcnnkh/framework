package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ServiceProvider;

public class NodeWriters extends ServiceProvider<NodeWriter, DOMException> implements NodeWriter {
	private ConversionService conversionService = SystemConversionService.getInstance();

	public NodeWriters() {
		setServiceClass(NodeWriter.class);
		getInjectors().register((service) -> {
			if (service instanceof ConversionServiceAware) {
				ConversionServiceAware conversionServiceAware = (ConversionServiceAware) service;
				conversionServiceAware.setConversionService(conversionService);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return optional().filter((e) -> e.isWriteable(sourceDescriptor)).isPresent();
	}

	@Override
	public void writeTo(TypedValue source, Node node) throws DOMException {
		NodeWriter documentWriter = optional().filter((e) -> e.isWriteable(source)).orElse(null);
		if (documentWriter == null) {
			throw new UnsupportedOperationException(source.toString());
		}
		documentWriter.writeTo(source, node);
	}

}
