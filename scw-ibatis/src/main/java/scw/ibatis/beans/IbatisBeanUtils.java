package scw.ibatis.beans;

import java.io.IOException;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeAliasRegistry;

import scw.beans.BeanFactory;
import scw.core.utils.StringUtils;
import scw.ibatis.IbatisException;
import scw.ibatis.beans.annotation.MapperResources;
import scw.ibatis.beans.annotation.MapperScan;
import scw.ibatis.beans.annotation.TypeAliase;
import scw.ibatis.beans.annotation.TypeAliaseScan;
import scw.io.Resource;
import scw.io.support.PathMatchingResourcePatternResolver;
import scw.logger.Logger;
import scw.logger.LoggerFactory;

public class IbatisBeanUtils {
	private static Logger logger = LoggerFactory.getLogger(IbatisBeanUtils.class);

	public static void configuration(Configuration configuration, BeanFactory beanFactory) {
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(
				beanFactory.getEnvironment());
		TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
		MapperRegistry mapperRegistry = configuration.getMapperRegistry();
		for (Class<?> clazz : beanFactory.getContextClasses()) {
			TypeAliase typeAliase = clazz.getAnnotation(TypeAliase.class);
			if (typeAliase != null) {
				typeAliases(typeAliasRegistry, typeAliase.value(), clazz);
			}
		}

		for (Class<?> clazz : beanFactory.getSourceClasses()) {
			MapperScan scan = clazz.getAnnotation(MapperScan.class);
			if (scan != null) {
				for (String packageName : scan.value()) {
					for (Class<?> mapperClass : beanFactory.getClassesLoaderFactory().getClassesLoader(packageName)) {
						registerMapper(mapperRegistry, mapperClass);
					}
				}
			}

			TypeAliaseScan typeAliaseScan = clazz.getAnnotation(TypeAliaseScan.class);
			if (typeAliaseScan != null) {
				for (String packageName : typeAliaseScan.value()) {
					for (Class<?> typeAliaseclass : beanFactory.getClassesLoaderFactory()
							.getClassesLoader(packageName)) {
						typeAliases(typeAliasRegistry, null, typeAliaseclass);
					}
				}
			}

			MapperResources mapperResources = clazz.getAnnotation(MapperResources.class);
			if (mapperResources != null) {
				for (String resourcePattern : mapperResources.value()) {
					Resource[] resources;
					try {
						resources = pathMatchingResourcePatternResolver.getResources(resourcePattern);
					} catch (IOException e) {
						logger.error(e, "Get resource error: {}", resourcePattern);
						continue;
					}

					for (Resource resource : resources) {
						registerMapper(configuration, resource);
					}
				}
			}
		}
	}

	public static void registerMapper(Configuration configuration, Resource resource) {
		try {
			resource.read((is) -> {
				XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, resource.getURI().toString(),
						configuration.getSqlFragments());
				builder.parse();
			});
		} catch (IOException e) {
			throw new IbatisException(resource.getDescription(), e);
		}
	}

	private static void registerMapper(MapperRegistry mapperRegistry, Class<?> clazz) {
		mapperRegistry.addMapper(clazz);
	}

	private static void typeAliases(TypeAliasRegistry typeAliasRegistry, String name, Class<?> clazz) {
		String nameToUse = StringUtils.isEmpty(name) ? clazz.getSimpleName() : name;
		if (typeAliasRegistry.resolveAlias(nameToUse) != null) {
			return;
		}
		typeAliasRegistry.registerAlias(nameToUse, clazz);
	}
}
