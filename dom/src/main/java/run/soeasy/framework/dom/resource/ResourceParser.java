package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.core.exe.Function;
import run.soeasy.framework.core.io.Resource;

public interface ResourceParser {
	boolean canParse(Resource resource);

	<T, E extends Throwable> T parse(Resource resource, Function<? super Node, ? extends T, ? extends E> processor)
			throws IOException, E;
}
