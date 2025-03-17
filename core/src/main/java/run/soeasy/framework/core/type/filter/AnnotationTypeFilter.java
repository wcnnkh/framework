package run.soeasy.framework.core.type.filter;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;

import run.soeasy.framework.core.annotation.AnnotationUtils;
import run.soeasy.framework.core.type.AnnotationMetadata;
import run.soeasy.framework.core.type.classreading.MetadataReader;
import run.soeasy.framework.util.ClassUtils;

/**
 * A simple {@link TypeFilter} which matches classes with a given annotation,
 * checking inherited annotations as well.
 *
 * <p>
 * By default, the matching logic mirrors that of
 * {@link AnnotationUtils#getAnnotation(java.lang.reflect.AnnotatedElement, Class)},
 * supporting annotations that are <em>present</em> or <em>meta-present</em> for
 * a single level of meta-annotations. The search for meta-annotations may be
 * disabled. Similarly, the search for annotations on interfaces may optionally
 * be enabled. Consult the various constructors in this class for details.
 *
 * @author Mark Fisher
 * @author Ramnivas Laddad
 * @author Juergen Hoeller
 * @author Sam Brannen
 */
public class AnnotationTypeFilter extends AbstractTypeHierarchyTraversingFilter {

	private final Class<? extends Annotation> annotationType;

	private final boolean considerMetaAnnotations;

	/**
	 * Create a new {@code AnnotationTypeFilter} for the given annotation type.
	 * <p>
	 * The filter will also match meta-annotations. To disable the meta-annotation
	 * matching, use the constructor that accepts a
	 * '{@code considerMetaAnnotations}' argument.
	 * <p>
	 * The filter will not match interfaces.
	 * 
	 * @param annotationType the annotation type to match
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType) {
		this(annotationType, true, false);
	}

	/**
	 * Create a new {@code AnnotationTypeFilter} for the given annotation type.
	 * <p>
	 * The filter will not match interfaces.
	 * 
	 * @param annotationType          the annotation type to match
	 * @param considerMetaAnnotations whether to also match on meta-annotations
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations) {
		this(annotationType, considerMetaAnnotations, false);
	}

	/**
	 * Create a new {@code AnnotationTypeFilter} for the given annotation type.
	 * 
	 * @param annotationType          the annotation type to match
	 * @param considerMetaAnnotations whether to also match on meta-annotations
	 * @param considerInterfaces      whether to also match interfaces
	 */
	public AnnotationTypeFilter(Class<? extends Annotation> annotationType, boolean considerMetaAnnotations,
			boolean considerInterfaces) {
		super(annotationType.isAnnotationPresent(Inherited.class), considerInterfaces);
		this.annotationType = annotationType;
		this.considerMetaAnnotations = considerMetaAnnotations;
	}

	/**
	 * Return the {@link Annotation} that this instance is using to filter
	 * candidates.
	 * 
	 */
	public final Class<? extends Annotation> getAnnotationType() {
		return this.annotationType;
	}

	@Override
	protected boolean matchSelf(MetadataReader metadataReader) {
		AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
		return metadata.hasAnnotation(this.annotationType.getName())
				|| (this.considerMetaAnnotations && metadata.hasMetaAnnotation(this.annotationType.getName()));
	}

	@Override
	
	protected Boolean matchSuperClass(String superClassName) {
		return hasAnnotation(superClassName);
	}

	@Override
	
	protected Boolean matchInterface(String interfaceName) {
		return hasAnnotation(interfaceName);
	}

	
	protected Boolean hasAnnotation(String typeName) {
		if (Object.class.getName().equals(typeName)) {
			return false;
		} else if (typeName.startsWith("java")) {
			if (!this.annotationType.getName().startsWith("java")) {
				// Standard Java types do not have non-standard annotations on them ->
				// skip any load attempt, in particular for Java language interfaces.
				return false;
			}
			try {
				Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
				return ((this.considerMetaAnnotations ? AnnotationUtils.getAnnotation(clazz, this.annotationType)
						: clazz.getAnnotation(this.annotationType)) != null);
			} catch (Throwable ex) {
				// Class not regularly loadable - can't determine a match that way.
			}
		}
		return null;
	}

}
