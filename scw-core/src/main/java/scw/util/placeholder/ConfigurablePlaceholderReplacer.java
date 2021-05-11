package scw.util.placeholder;

public interface ConfigurablePlaceholderReplacer extends PlaceholderReplacer,
		Iterable<PlaceholderReplacer> {
	void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer);
}
