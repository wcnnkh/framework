package io.basc.framework.dom;

import io.basc.framework.io.Resource;

import org.w3c.dom.Document;

public interface DocumentReader {
	boolean canReader(Resource resource);
	
	Document read(Resource resource);
}
