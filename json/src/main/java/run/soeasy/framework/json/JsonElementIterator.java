package run.soeasy.framework.json;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.core.collection.CloseableIterator;

@RequiredArgsConstructor
public class JsonElementIterator implements CloseableIterator<JsonElement>{
	@NonNull
	private final JsonNode jsonNode;

	@Override
	public boolean hasNext() {
		
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public JsonElement next() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}
}
