package io.basc.framework.net.multipart;

import java.io.IOException;
import java.util.List;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.net.InputMessage;

public class ConfigurableMultipartMessageResolver extends ConfigurableServices<MultipartMessageResolver>
		implements MultipartMessageResolver {

	public ConfigurableMultipartMessageResolver() {
		setServiceClass(MultipartMessageResolver.class);
	}

	@Override
	public boolean isMultipart(InputMessage inputMessage) {
		for (MultipartMessageResolver resolver : getServices()) {
			if (resolver.isMultipart(inputMessage)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public List<MultipartMessage> resolve(InputMessage inputMessage) throws IOException {
		for (MultipartMessageResolver resolver : getServices()) {
			if (resolver.isMultipart(inputMessage)) {
				return resolver.resolve(inputMessage);
			}
		}
		throw new NotFoundException("Unable to find corresponding MultipartMessage Resolver");
	}

}
