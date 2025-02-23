package io.basc.framework.net.multipart;

import java.io.IOException;
import java.util.List;

import io.basc.framework.net.InputMessage;
import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurableMultipartMessageResolver extends ConfigurableServices<MultipartMessageResolver>
		implements MultipartMessageResolver {

	public ConfigurableMultipartMessageResolver() {
		setServiceClass(MultipartMessageResolver.class);
	}

	@Override
	public boolean isMultipart(InputMessage inputMessage) {
		for (MultipartMessageResolver resolver : this) {
			if (resolver.isMultipart(inputMessage)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<MultipartMessage> resolve(InputMessage inputMessage) throws IOException {
		for (MultipartMessageResolver resolver : this) {
			if (resolver.isMultipart(inputMessage)) {
				return resolver.resolve(inputMessage);
			}
		}
		throw new UnsupportedOperationException("Unable to find corresponding MultipartMessage Resolver");
	}

}
