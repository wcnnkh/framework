package scw.json;

public abstract class AbstractJson<K> implements Json<K> {

	@Override
	public String toString() {
		return toJSONString();
	}
}
