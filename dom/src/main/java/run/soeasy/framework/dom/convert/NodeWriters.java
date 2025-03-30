package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.Source;
import run.soeasy.framework.core.convert.SourceDescriptor;
import run.soeasy.framework.core.convert.service.ConversionService;
import run.soeasy.framework.core.convert.service.ConversionServiceAware;
import run.soeasy.framework.core.convert.support.DefaultConversionService;
import run.soeasy.framework.util.exchange.Registration;
import run.soeasy.framework.util.spi.Providers;

public class NodeWriters extends Providers<NodeWriter, DOMException> implements NodeWriter {
	private ConversionService conversionService = DefaultConversionService.getInstance();

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
	public void writeTo(Source source, Node node) throws DOMException {
		NodeWriter documentWriter = optional().filter((e) -> e.isWriteable(source)).orElse(null);
		if (documentWriter == null) {
			throw new UnsupportedOperationException(source.toString());
		}
		documentWriter.writeTo(source, node);
	}

}
