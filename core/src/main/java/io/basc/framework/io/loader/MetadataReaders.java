package io.basc.framework.io.loader;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import io.basc.framework.core.type.classreading.CachingMetadataReaderFactory;
import io.basc.framework.core.type.classreading.MetadataReader;
import io.basc.framework.core.type.classreading.MetadataReaderFactory;
import io.basc.framework.core.type.classreading.SimpleMetadataReaderFactory;
import io.basc.framework.core.type.filter.TypeFilter;
import io.basc.framework.io.FileSystemResource;
import io.basc.framework.io.FileSystemResourceLoader;
import io.basc.framework.io.FileUtils;
import io.basc.framework.io.Resource;
import io.basc.framework.io.ResourcePatternResolver;
import io.basc.framework.lang.Nullable;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.spi.Services;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ClassUtils;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * MetadataReader注册
 * 
 * @author wcnnkh
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MetadataReaders extends Services<MetadataReader> {
	private static Logger logger = LoggerFactory.getLogger(MetadataReaders.class);
	private TypeFilter includeTypeFilter;

	public MetadataReaders andIncludeTypeFilter(TypeFilter includeTypeFilter) {
		Assert.requiredArgument(includeTypeFilter != null, "includeTypeFilter");
		if (this.includeTypeFilter == null) {
			this.includeTypeFilter = includeTypeFilter;
		} else {
			this.includeTypeFilter = this.includeTypeFilter.and(includeTypeFilter);
		}
		return this;
	}

	public final Registration includeDirectory(File directory, @Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(directory != null, "directory");
		MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(new FileSystemResourceLoader());
		return includeResources(metadataReaderFactory,
				FileUtils.listAllFiles(directory)
						.filter((e) -> !e.isDirectory() && e.getName().endsWith(ClassUtils.CLASS_FILE_SUFFIX))
						.map(FileSystemResource::new),
				typeFilter);
	}

	public final Registration includeLocation(ResourcePatternResolver resourcePatternResolver, String location,
			@Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(location != null, "location");
		return includeLocationPattern(resourcePatternResolver,
				location.endsWith(ClassUtils.CLASS_FILE_SUFFIX) ? location
						: (location.endsWith("/") ? (location + "**/*" + ClassUtils.CLASS_FILE_SUFFIX)
								: (location + "/**/*" + ClassUtils.CLASS_FILE_SUFFIX)),
				typeFilter);
	}

	public Registration includeLocationPattern(ResourcePatternResolver resourcePatternResolver, String locationPattern,
			@Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(resourcePatternResolver != null, "resourcePatternResolver");
		Assert.requiredArgument(locationPattern != null, "locationPattern");
		MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
		return includeResources(metadataReaderFactory, Elements.of(() -> {
			Resource[] resources = null;
			try {
				resources = resourcePatternResolver.getResources(locationPattern);
			} catch (IOException e) {
				logger.error(e, "Failed to obtain {} to obtain resources", locationPattern);
			}

			if (resources == null || resources.length == 0) {
				return Stream.empty();
			}

			return Stream.of(resources);
		}), typeFilter);
	}

	public final Registration includePackage(ResourcePatternResolver resourcePatternResolver, String packageName,
			@Nullable TypeFilter typeFilter) {
		return includeLocation(resourcePatternResolver, ClassUtils.convertClassNameToResourcePath(packageName),
				typeFilter);
	}

	public Registration includeResources(MetadataReaderFactory metadataReaderFactory,
			Elements<? extends Resource> resources, @Nullable TypeFilter typeFilter) {
		Assert.requiredArgument(metadataReaderFactory != null, "metadataReaderFactory");
		Assert.requiredArgument(resources != null, "resources");
		TypeFilter typeFilterToUse = getIncludeTypeFilter();
		if (typeFilter != null) {
			if (typeFilterToUse == null) {
				typeFilterToUse = typeFilter;
			} else {
				typeFilterToUse = typeFilterToUse.and(typeFilter);
			}
		}

		MetadataReaderLoader metadataReaderLoader = new MetadataReaderLoader(metadataReaderFactory, resources,
				typeFilterToUse);
		return getServiceLoaders().register(metadataReaderLoader);
	}

	public MetadataReaders orIncludeTypeFilter(TypeFilter includeTypeFilter) {
		Assert.requiredArgument(includeTypeFilter != null, "includeTypeFilter");
		if (this.includeTypeFilter == null) {
			this.includeTypeFilter = includeTypeFilter;
		} else {
			this.includeTypeFilter = this.includeTypeFilter.or(includeTypeFilter);
		}
		return this;
	}
}
