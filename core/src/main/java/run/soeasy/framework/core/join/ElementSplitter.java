package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.io.IOUtils;

@Getter
@Setter
public class ElementSplitter extends ElementJoining<Object> implements Splitter<String> {
	@NonNull
	private final Function<String, String> decoder;

	public ElementSplitter(@NonNull CharSequence delimiter, @NonNull Codec<String, String> codec) {
		super(delimiter, (value) -> value == null ? null : codec.encode(String.valueOf(value)));
		this.decoder = (value) -> value == null ? null : codec.decode(value);
	}

	@Override
	public Stream<String> split(@NonNull Readable readable) throws IOException {
		return IOUtils.split(readable, getDelimiter()).map((e) -> e == null ? null : e.toString()).map(decoder);
	}
}
