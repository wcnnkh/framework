package io.basc.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Pipeline;
import io.basc.framework.util.io.Resource;

public interface DocumentParser {
	boolean canParse(Resource resource);

	@Nullable
	<T, E extends Throwable> T parse(Resource resource, Pipeline<? super Document, ? extends T, ? extends E> processor)
			throws IOException, E;
}
