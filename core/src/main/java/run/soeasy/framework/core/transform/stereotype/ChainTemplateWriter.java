package run.soeasy.framework.core.transform.stereotype;

import java.util.Iterator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChainTemplateWriter<K, V extends Accessor, T extends Template<K, V>> implements TemplateWriter<K, V, T> {
	@NonNull
	private final Iterator<? extends TemplateWriteFilter<K, V, T>> iterator;
	private TemplateWriter<K, V, T> templateWriter;

	@Override
	public boolean writeTo(@NonNull TemplateContext<K, V, T> sourceContext,
			@NonNull TemplateContext<K, V, T> targetContext) {
		if (iterator.hasNext()) {
			return iterator.next().writeTo(sourceContext, targetContext, this);
		} else if (templateWriter != null) {
			return templateWriter.writeTo(sourceContext, targetContext);
		}
		return false;
	}
}
