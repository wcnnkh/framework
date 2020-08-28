package scw.compatible.map;

public class Java5MapWrapper implements MapCompatible {

	public <K, V> CompatibleMap<K, V> wrapper(java.util.Map<K, V> map) {
		return new DefaultCompatibleMap<K, V>(map);
	}
}
