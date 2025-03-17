package run.soeasy.framework.dom;

import java.io.IOException;

import org.w3c.dom.Document;

import run.soeasy.framework.util.function.Function;
import run.soeasy.framework.util.io.Resource;

public interface DocumentParser {
	boolean canParse(Resource resource);

	<T, E extends Throwable> T parse(Resource resource, Function<? super Document, ? extends T, ? extends E> processor)
			throws IOException, DomException, E;
}
