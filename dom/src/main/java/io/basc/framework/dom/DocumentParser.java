package io.basc.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.function.Function;
import io.basc.framework.util.io.Resource;

public interface DocumentParser {
	boolean canParse(Resource resource);

	@Nullable
	<T, E extends Throwable> T parse(Resource resource, Function<? super Document, ? extends T, ? extends E> processor)
			throws IOException, E;
}
