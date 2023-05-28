package io.basc.framework.ibatis.beans;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.builder.annotation.MapperAnnotationBuilder;
import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeAliasRegistry;

import io.basc.framework.beans.ConfigurableBeanFactory;
import io.basc.framework.beans.config.BeanDefinition;
import io.basc.framework.context.Context;
import io.basc.framework.factory.InstanceFactory;
import io.basc.framework.ibatis.IbatisException;
import io.basc.framework.ibatis.beans.annotation.MapperResources;
import io.basc.framework.ibatis.beans.annotation.MapperScan;
import io.basc.framework.ibatis.beans.annotation.TypeAliase;
import io.basc.framework.ibatis.beans.annotation.TypeAliaseScan;
import io.basc.framework.io.Resource;
import io.basc.framework.io.support.PathMatchingResourcePatternResolver;
import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.XUtils;

public class ConfigurationUtils {
	private static Logger logger = LoggerFactory.getLogger(ConfigurationUtils.class);

	public static void configurationEnvironment(Configuration configuration, InstanceFactory instanceFactory) {
		Environment environment = configuration.getEnvironment();
		if (environment == null) {
			// 创建Environment
			if (instanceFactory.isInstance(DataSource.class)) {
				environment = new Environment(XUtils.getUUID(), new JdbcTransactionFactory(),
						instanceFactory.getInstance(DataSource.class));
				configuration.setEnvironment(environment);
			}
		} else {
			DataSource dataSource = environment.getDataSource();
			if (dataSource == null && instanceFactory.isInstance(DataSource.class)) {
				dataSource = instanceFactory.getInstance(DataSource.class);
				environment = new Environment(environment.getId(), environment.getTransactionFactory(), dataSource);
				configuration.setEnvironment(environment);
			}
		}
	}

	public static void configuration(Configuration configuration, Context context) {
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(
				context.getResourceLoader());
		TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();
		for (Class<?> clazz : context.getContextClasses()) {
			TypeAliase typeAliase = clazz.getAnnotation(TypeAliase.class);
			if (typeAliase != null) {
				typeAliases(typeAliasRegistry, typeAliase.value(), clazz);
			}

			if (clazz.isAnnotationPresent(Mapper.class)) {
				registerMapper(configuration, clazz);
			}
		}

		for (Class<?> clazz : context.getContextClasses()) {
			MapperScan scan = clazz.getAnnotation(MapperScan.class);
			if (scan != null) {
				for (String packageName : scan.value()) {
					for (Class<?> mapperClass : context.getClassScanner().scan(packageName,
							(e, m) -> e.getClassMetadata().isInterface())) {
						registerMapper(configuration, mapperClass);
					}
				}
			}

			TypeAliaseScan typeAliaseScan = clazz.getAnnotation(TypeAliaseScan.class);
			if (typeAliaseScan != null) {
				for (String packageName : typeAliaseScan.value()) {
					for (Class<?> typeAliaseclass : context.getClassScanner().scan(packageName, null)) {
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
			resource.consume((is) -> {
				XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, resource.getURI().toString(),
						configuration.getSqlFragments());
				builder.parse();
			});
		} catch (IOException e) {
			throw new IbatisException(resource.getDescription(), e);
		}
	}

	public static void registerMapper(Configuration configuration, Class<?> clazz) {
		configuration.addMapper(clazz);
		MapperAnnotationBuilder annotationBuilder = new MapperAnnotationBuilder(configuration, clazz);
		annotationBuilder.parse();
	}

	private static void typeAliases(TypeAliasRegistry typeAliasRegistry, String name, Class<?> clazz) {
		String nameToUse = StringUtils.isEmpty(name) ? clazz.getSimpleName() : name;
		if (typeAliasRegistry.resolveAlias(nameToUse) != null) {
			return;
		}
		typeAliasRegistry.registerAlias(nameToUse, clazz);
	}

	public static void registerMapperDefinition(ConfigurableBeanFactory beanFactory, Class<?> mapperClass) {
		BeanDefinition definition = new MapperBeanDefinition(beanFactory, mapperClass);
		if (!beanFactory.containsDefinition(definition.getId())) {
			beanFactory.registerDefinition(definition);
		}
	}

	public static Configuration build(Resource resource) {
		Assert.requiredArgument(resource != null, "resource");
		try {
			return resource.read((is) -> {
				XMLConfigBuilder builder = new XMLConfigBuilder(is);
				builder.parse();
				return builder.getConfiguration();
			});
		} catch (IOException e) {
			throw new IbatisException(resource.getDescription(), e);
		}
	}
}
