package run.soeasy.framework.core.join;

import java.io.IOException;
import java.util.function.Function;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ElementJoining<E> implements Joiner<E> {
	private final CharSequence delimiter;
	@NonNull
	private final Function<? super E, ? extends CharSequence> encoder;

	@Override
	public long join(Appendable appendable, long count, E element) throws IOException {
		CharSequence charSequence = encoder.apply(element);
		if (charSequence == null) {
			return 0;
		}

		if (count != 0) {
			appendable.append(delimiter);
		}
		appendable.append(charSequence);
		return 1;
	}
}
