package io.basc.framework.net.message.multipart;

import java.io.IOException;
import java.util.List;

import io.basc.framework.beans.factory.config.ConfigurableServices;
import io.basc.framework.lang.NotFoundException;
import io.basc.framework.net.message.InputMessage;

public class MultipartMessageResolvers extends ConfigurableServices<MultipartMessageResolver>
		implements MultipartMessageResolver {

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
