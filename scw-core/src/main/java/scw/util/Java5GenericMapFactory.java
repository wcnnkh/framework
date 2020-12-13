package scw.util;


class Java5GenericMapFactory implements GenericMapFactory {

	public <K, V> GenericMap<K, V> wrapper(java.util.Map<K, V> map) {
		return new DefaultGenericMap<K, V>(map);
	}
}
