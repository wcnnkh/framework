package run.soeasy.framework.dom.convert;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

import run.soeasy.framework.core.convert.ConversionService;
import run.soeasy.framework.core.convert.ConverterAware;
import run.soeasy.framework.core.convert.support.SystemConversionService;
import run.soeasy.framework.core.convert.value.SourceDescriptor;
import run.soeasy.framework.core.convert.value.TypedValue;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.spi.ConfigurableServices;

public class NodeWriters extends ConfigurableServices<NodeWriter> implements NodeWriter {
	private ConversionService conversionService = SystemConversionService.getInstance();

	public NodeWriters() {
		setServiceClass(NodeWriter.class);
		getInjectors().register((service) -> {
			if (service instanceof ConverterAware) {
				ConverterAware converterAware = (ConverterAware) service;
				converterAware.setConverter(conversionService);
			}
			return Registration.SUCCESS;
		});
	}

	@Override
	public boolean isWriteable(SourceDescriptor sourceDescriptor) {
		return anyMatch((e) -> e.isWriteable(sourceDescriptor));
	}

	@Override
	public void writeTo(TypedValue source, Node node) throws DOMException {
		for (NodeWriter writer : this) {
			if (writer.isWriteable(source)) {
				writer.writeTo(source, node);
				return;
			}
		}
		throw new UnsupportedOperationException(source.toString());
	}

}
