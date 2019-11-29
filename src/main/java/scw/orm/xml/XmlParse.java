package scw.orm.xml;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Node;

public interface XmlParse {
	<T> T parse(Class<? extends T> clazz, Node node) throws Exception;

	<T> List<T> parseList(Class<? extends T> clazz, Node node) throws Exception;

	<K, V> Map<K, V> parseMap(Class<? extends V> clazz, Node node) throws Exception;
}
