package io.basc.framework.net.multipart;

import java.io.IOException;
import java.util.List;

import io.basc.framework.net.InputMessage;

public class DefaultMultipartMessageResolver extends ConfigurableMultipartMessageResolver {
	private final MultipartMessageResolver backpackMultipartMessageResolver = GlobalMultipartMessageResolver
			.getInstance();

	@Override
	public boolean isMultipart(InputMessage inputMessage) {
		return super.isMultipart(inputMessage) || backpackMultipartMessageResolver.isMultipart(inputMessage);
	}

	@Override
	public List<MultipartMessage> resolve(InputMessage inputMessage) throws IOException {
		if (super.isMultipart(inputMessage)) {
			return super.resolve(inputMessage);
		}
		return backpackMultipartMessageResolver.resolve(inputMessage);
	}
}
