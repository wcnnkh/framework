package scw.lucene;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockFactory;

import scw.beans.AbstractBeanDefinition;
import scw.beans.BeanDefinition;
import scw.beans.builder.BeanBuilderLoader;
import scw.beans.builder.BeanBuilderLoaderChain;
import scw.beans.builder.LoaderContext;
import scw.core.instance.annotation.Configuration;
import scw.core.utils.StringUtils;

@Configuration
public class LuceneBeanBuilderLoader implements BeanBuilderLoader {

	public BeanDefinition loading(LoaderContext context, BeanBuilderLoaderChain loaderChain) {
		if (context.getTargetClass() == Directory.class) {
			return new DirectorBeanBuilder(context);
		}

		if (context.getTargetClass() == Analyzer.class) {
			return new AnalyzerBeanBuilder(context);
		}

		return loaderChain.loading(context);
	}

	private static class DirectorBeanBuilder extends AbstractBeanDefinition {

		public DirectorBeanBuilder(LoaderContext context) {
			super(context);
		}

		public String getDirectory() {
			return propertyFactory.getString("lucene.directory");
		}

		public boolean isInstance() {
			return StringUtils.isNotEmpty(getDirectory());
		}

		public Object create() throws Exception {
			Path path = Paths.get(getDirectory());
			if (beanFactory.isInstance(LockFactory.class)) {
				return FSDirectory.open(path, beanFactory.getInstance(LockFactory.class));
			} else {
				return FSDirectory.open(path);
			}
		}
	}

	private static class AnalyzerBeanBuilder extends AbstractBeanDefinition {

		public AnalyzerBeanBuilder(LoaderContext context) {
			super(context);
		}

		public boolean isInstance() {
			return true;
		}

		public Object create() throws Exception {
			return new StandardAnalyzer();
		}
	}
}
