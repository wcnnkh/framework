package io.basc.framework.core.type.classreading;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;

import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.type.AnnotationMetadata;
import io.basc.framework.core.type.MethodMetadata;

/**
 * {@link AnnotationMetadata} created from a
 * {@link SimpleAnnotationMetadataReadingVisitor}.
 *
 * @author Phillip Webb
 * @author Sam Brannen
 */
final class SimpleAnnotationMetadata implements AnnotationMetadata {

	private final String className;

	private final int access;

	
	private final String enclosingClassName;

	
	private final String superClassName;

	private final boolean independentInnerClass;

	private final String[] interfaceNames;

	private final String[] memberClassNames;

	private final MethodMetadata[] annotatedMethods;

	private final MergedAnnotations annotations;

	
	private Set<String> annotationTypes;

	SimpleAnnotationMetadata(String className, int access,  String enclosingClassName,
			 String superClassName, boolean independentInnerClass, String[] interfaceNames,
			String[] memberClassNames, MethodMetadata[] annotatedMethods, MergedAnnotations annotations) {

		this.className = className;
		this.access = access;
		this.enclosingClassName = enclosingClassName;
		this.superClassName = superClassName;
		this.independentInnerClass = independentInnerClass;
		this.interfaceNames = interfaceNames;
		this.memberClassNames = memberClassNames;
		this.annotatedMethods = annotatedMethods;
		this.annotations = annotations;
	}

	@Override
	public String getClassName() {
		return this.className;
	}

	@Override
	public boolean isInterface() {
		return (this.access & Opcodes.ACC_INTERFACE) != 0;
	}

	@Override
	public boolean isAnnotation() {
		return (this.access & Opcodes.ACC_ANNOTATION) != 0;
	}

	@Override
	public boolean isAbstract() {
		return (this.access & Opcodes.ACC_ABSTRACT) != 0;
	}

	@Override
	public boolean isFinal() {
		return (this.access & Opcodes.ACC_FINAL) != 0;
	}

	@Override
	public boolean isIndependent() {
		return (this.enclosingClassName == null || this.independentInnerClass);
	}

	@Override
	
	public String getEnclosingClassName() {
		return this.enclosingClassName;
	}

	@Override
	
	public String getSuperClassName() {
		return this.superClassName;
	}

	@Override
	public String[] getInterfaceNames() {
		return this.interfaceNames.clone();
	}

	@Override
	public String[] getMemberClassNames() {
		return this.memberClassNames.clone();
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return this.annotations;
	}

	@Override
	public Set<String> getAnnotationTypes() {
		Set<String> annotationTypes = this.annotationTypes;
		if (annotationTypes == null) {
			annotationTypes = Collections.unmodifiableSet(AnnotationMetadata.super.getAnnotationTypes());
			this.annotationTypes = annotationTypes;
		}
		return annotationTypes;
	}

	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		Set<MethodMetadata> annotatedMethods = null;
		for (MethodMetadata annotatedMethod : this.annotatedMethods) {
			if (annotatedMethod.isAnnotated(annotationName)) {
				if (annotatedMethods == null) {
					annotatedMethods = new LinkedHashSet<>(4);
				}
				annotatedMethods.add(annotatedMethod);
			}
		}
		return (annotatedMethods != null ? annotatedMethods : Collections.emptySet());
	}

	@Override
	public boolean equals( Object obj) {
		return ((this == obj) || ((obj instanceof SimpleAnnotationMetadata)
				&& this.className.equals(((SimpleAnnotationMetadata) obj).className)));
	}

	@Override
	public int hashCode() {
		return this.className.hashCode();
	}

	@Override
	public String toString() {
		return this.className;
	}

	@Override
	public boolean isPublic() {
		return (this.access & Opcodes.ACC_PUBLIC) != 0;
	}

	@Override
	public boolean isEnum() {
		return (this.access & Opcodes.ACC_ENUM) != 0;
	}

}
