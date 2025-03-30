package run.soeasy.framework.dom.resource;

import java.io.IOException;

import org.w3c.dom.Node;

import run.soeasy.framework.util.io.OutputStreamFactory;

public interface ResourceTransformer {
	boolean canTransform(Node source);

	void transform(Node node, OutputStreamFactory<?> target) throws IOException;
}
