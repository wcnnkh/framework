package scw.application.embedded.tomcat;

import javax.servlet.ServletContext;

import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScannerCallback;
import org.apache.tomcat.util.scan.StandardJarScanner;

import scw.core.PropertyFactory;

/**
 * tomcat8以上
 * 
 * @author shuchaowen
 *
 */
public class Tomcat8AboveStandardJarScanner extends StandardJarScanner {
	public Tomcat8AboveStandardJarScanner(PropertyFactory propertyFactory) {
		setScanManifest(false);
	}

	@Override
	public void scan(JarScanType scanType, ServletContext context,
			JarScannerCallback callback) {
		if(scanType == JarScanType.TLD){
			return ;
		}
		super.scan(scanType, context, callback);
	}
}
