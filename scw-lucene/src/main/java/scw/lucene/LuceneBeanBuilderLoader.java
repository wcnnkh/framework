package scw.lucene;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;

import scw.beans.BeanDefinition;
import scw.beans.BeanDefinitionLoader;
import scw.beans.BeanDefinitionLoaderChain;
import scw.beans.BeanFactory;
import scw.beans.BeansException;
import scw.beans.support.DefaultBeanDefinition;
import scw.context.annotation.Provider;
import scw.core.utils.StringUtils;

@Provider
public class LuceneBeanBuilderLoader implements BeanDefinitionLoader {

	public BeanDefinition load(BeanFactory beanFactory, Class<?> sourceClass, BeanDefinitionLoaderChain loaderChain) {
		if (sourceClass == Directory.class) {
			return new DirectorBeanBuilder(beanFactory, sourceClass);
		}

		if (sourceClass == Analyzer.class) {
			return new AnalyzerBeanBuilder(beanFactory, sourceClass);
		}

		return loaderChain.load(beanFactory, sourceClass);
	}

	private static class DirectorBeanBuilder extends DefaultBeanDefinition {

		public DirectorBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public String getDirectory() {
			return beanFactory.getEnvironment().getString("lucene.directory");
		}

		public boolean isInstance() {
			return StringUtils.isNotEmpty(getDirectory());
		}

		public Object create() throws BeansException {
			Path path = Paths.get(getDirectory());
			if (beanFactory.isInstance(LockFactory.class)) {
				try {
					return FSDirectory.open(path, beanFactory.getInstance(LockFactory.class));
				} catch (IOException e) {
					throw new BeansException(path.toString(), e);
				}
			} else {
				try {
					return FSDirectory.open(path);
				} catch (IOException e) {
					throw new BeansException(path.toString(), e);
				}
			}
		}
	}

	private static class AnalyzerBeanBuilder extends DefaultBeanDefinition {

		public AnalyzerBeanBuilder(BeanFactory beanFactory, Class<?> sourceClass) {
			super(beanFactory, sourceClass);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws BeansException {
			return new StandardAnalyzer();
		}
	}
}
