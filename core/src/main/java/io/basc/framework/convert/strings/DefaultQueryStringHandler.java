package io.basc.framework.convert.strings;

import java.io.IOException;
import java.util.concurrent.atomic.LongAdder;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Pair;
import io.basc.framework.util.function.ConsumeProcessor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DefaultQueryStringHandler implements QueryStringHandler {
	private String connector;
	private String keyValueConnector;
	private Codec<CharSequence, CharSequence> codec;

	public DefaultQueryStringHandler() {
		this("&", "?");
	}

	public DefaultQueryStringHandler(String connector, String keyValueConnector) {
		this.connector = connector;
		this.keyValueConnector = keyValueConnector;
	}

	@Override
	public void write(LongAdder writeCount, CharSequence key, CharSequence value, Appendable target)
			throws IOException {
		if (writeCount.longValue() == 0) {
			target.append(connector);
			writeCount.increment();
		}

		if (codec != null) {
			key = codec.encode(key);
			value = codec.encode(value);
		}

		target.append(key);
		target.append(keyValueConnector);
		target.append(value);
	}

	@Override
	public <E extends Throwable> void read(LongAdder readCount, Readable source,
			ConsumeProcessor<? super Pair<? extends CharSequence, ? extends CharSequence>, ? extends E> consumer)
			throws IOException, E {
		// TODO Auto-generated method stub
		
	}

}
