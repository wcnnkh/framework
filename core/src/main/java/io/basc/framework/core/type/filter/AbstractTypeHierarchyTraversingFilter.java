package io.basc.framework.core.type.filter;

import java.io.IOException;

import io.basc.framework.core.type.ClassMetadata;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

/**
 * Type filter that is aware of traversing over hierarchy.
 *
 * <p>
 * This filter is useful when matching needs to be made based on potentially the
 * whole class/interface hierarchy. The algorithm employed uses a succeed-fast
 * strategy: if at any time a match is declared, no further processing is
 * carried out.
 *
 * @author Ramnivas Laddad
 * @author Mark Fisher
 */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {
	private static Logger logger = LoggerFactory.getLogger(AbstractTypeHierarchyTraversingFilter.class);

	private final boolean considerInherited;

	private final boolean considerInterfaces;

	protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
		this.considerInherited = considerInherited;
		this.considerInterfaces = considerInterfaces;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
			throws IOException {

		// This method optimizes avoiding unnecessary creation of ClassReaders
		// as well as visiting over those readers.
		if (matchSelf(metadataReader)) {
			return true;
		}
		ClassMetadata metadata = metadataReader.getClassMetadata();
		if (matchClassName(metadata.getClassName())) {
			return true;
		}

		if (this.considerInherited) {
			String superClassName = metadata.getSuperClassName();
			if (superClassName != null) {
				// Optimization to avoid creating ClassReader for superclass.
				Boolean superClassMatch = matchSuperClass(superClassName);
				if (superClassMatch != null) {
					if (superClassMatch.booleanValue()) {
						return true;
					}
				} else {
					// Need to read superclass to determine a match...
					try {
						if (match(metadata.getSuperClassName(), metadataReaderFactory)) {
							return true;
						}
					} catch (IOException ex) {
						if (logger.isDebugEnabled()) {
							logger.debug("Could not read superclass [" + metadata.getSuperClassName()
									+ "] of type-filtered class [" + metadata.getClassName() + "]");
						}
					}
				}
			}
		}

		if (this.considerInterfaces) {
			for (String ifc : metadata.getInterfaceNames()) {
				// Optimization to avoid creating ClassReader for superclass
				Boolean interfaceMatch = matchInterface(ifc);
				if (interfaceMatch != null) {
					if (interfaceMatch.booleanValue()) {
						return true;
					}
				} else {
					// Need to read interface to determine a match...
					try {
						if (match(ifc, metadataReaderFactory)) {
							return true;
						}
					} catch (IOException ex) {
						if (logger.isDebugEnabled()) {
							logger.debug("Could not read interface [" + ifc + "] for type-filtered class ["
									+ metadata.getClassName() + "]");
						}
					}
				}
			}
		}

		return false;
	}

	private boolean match(String className, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
	}

	/**
	 * Override this to match self characteristics alone. Typically, the
	 * implementation will use a visitor to extract information to perform matching.
	 */
	protected boolean matchSelf(MetadataReader metadataReader) {
		return false;
	}

	/**
	 * Override this to match on type name.
	 */
	protected boolean matchClassName(String className) {
		return false;
	}

	/**
	 * Override this to match on supertype name.
	 */
	@Nullable
	protected Boolean matchSuperClass(String superClassName) {
		return null;
	}

	/**
	 * Override this to match on interface type name.
	 */
	@Nullable
	protected Boolean matchInterface(String interfaceName) {
		return null;
	}

}
