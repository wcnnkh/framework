package run.soeasy.framework.core.collection;

import java.io.Serializable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@Getter
public class StandardIterableElements<E, W extends Iterable<E>> implements IterableElementsWrapper<E, W>, Serializable {
	private static final long serialVersionUID = 1L;
	@NonNull
	private W source;
}