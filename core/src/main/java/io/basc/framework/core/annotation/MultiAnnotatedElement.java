package io.basc.framework.core.annotation;

import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public class MultiAnnotatedElement extends AnnotatedElements {

	public static AnnotatedElement forAnnotatedElements(AnnotatedElement... annotatedElements) {
		return new MultiAnnotatedElement(annotatedElements);
	}

	private final Iterable<? extends AnnotatedElement> annotatedElements;

	public MultiAnnotatedElement(AnnotatedElement... annotatedElements) {
		this.annotatedElements = annotatedElements == null ? Collections.emptyList() : Arrays.asList(annotatedElements);
	}

	public MultiAnnotatedElement(Iterable<? extends AnnotatedElement> annotatedElements) {
		this.annotatedElements = annotatedElements == null ? Collections.emptyList() : annotatedElements;
	}

	@Override
	protected Iterator<? extends AnnotatedElement> annotationElementIterator() {
		return annotatedElements.iterator();
	}

	@Override
	public String toString() {
		return "MultiAnnotatedElement(annotatedElements=" + annotatedElements.toString() + ")";
	}
}