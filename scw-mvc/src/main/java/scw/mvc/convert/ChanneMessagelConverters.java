package scw.mvc.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import scw.convert.TypeDescriptor;
import scw.core.OrderComparator;
import scw.core.parameter.ParameterDescriptor;
import scw.http.server.ServerHttpRequest;
import scw.http.server.ServerHttpResponse;

public class ChanneMessagelConverters implements ChannelMessagelConverter {
	private volatile List<ChannelMessagelConverter> converters;

	public ChanneMessagelConverters() {
	}

	public void addMessageConverter(ChannelMessagelConverter converter) {
		synchronized (this) {
			if (converters == null) {
				converters = new ArrayList<ChannelMessagelConverter>(8);
			}

			converters.add(converter);
			Collections.sort(converters, OrderComparator.INSTANCE.reversed());
		}
	}

	@Override
	public boolean canRead(ParameterDescriptor parameterDescriptor) {
		for (ChannelMessagelConverter converter : converters) {
			if (converter.canRead(parameterDescriptor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Object read(ParameterDescriptor parameterDescriptor,
			ServerHttpRequest request) throws IOException,
			ChannelMessagelConverterException {
		for (ChannelMessagelConverter converter : converters) {
			if (converter.canRead(parameterDescriptor)) {
				return converter.read(parameterDescriptor, request);
			}
		}
		throw new ChannelMessagelConverterException(parameterDescriptor,
				request, null);
	}

	@Override
	public boolean canWrite(TypeDescriptor type, Object body) {
		for (ChannelMessagelConverter converter : converters) {
			if (converter.canWrite(type, body)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void write(TypeDescriptor type, Object body,
			ServerHttpRequest request, ServerHttpResponse response)
			throws IOException, ChannelMessagelConverterException {
		for (ChannelMessagelConverter converter : converters) {
			if (converter.canWrite(type, body)) {
				converter.write(type, body, request, response);
				return;
			}
		}
		throw new ChannelMessagelConverterException(type, body, request, null);
	}

}
