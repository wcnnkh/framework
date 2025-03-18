package run.soeasy.framework.messaging.multipart;

import java.io.IOException;
import java.util.List;

import run.soeasy.framework.messaging.InputMessage;

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
