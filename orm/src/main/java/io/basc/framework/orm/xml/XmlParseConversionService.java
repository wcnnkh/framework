package io.basc.framework.orm.xml;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionService;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.io.Resource;
import io.basc.framework.xml.XmlUtils;

public class XmlParseConversionService implements ConversionService{

	public boolean canConvert(TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		return Document.class.isAssignableFrom(targetType.getType()) && 
				(InputStream.class.isAssignableFrom(sourceType.getType()) 
				|| Reader.class.isAssignableFrom(sourceType.getType())
				|| String.class.isAssignableFrom(sourceType.getType())
				|| InputSource.class.isAssignableFrom(sourceType.getType())
				|| File.class.isAssignableFrom(sourceType.getType())
				|| Resource.class.isAssignableFrom(sourceType.getType())
				);
	}

	public Object convert(Object source, TypeDescriptor sourceType,
			TypeDescriptor targetType) {
		if(InputStream.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().getParser().parse((InputStream)source);
		}else if(Reader.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().getParser().parse((Reader)source);
		}else if(String.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().getParser().parse((String)source);
		}else if(InputSource.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().getParser().parse((InputSource)source);
		}else if(File.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().getParser().parse((File)source);
		}else if(Resource.class.isAssignableFrom(sourceType.getType())){
			return XmlUtils.getTemplate().parse((Resource)source, (dom) -> dom);
		}
		throw new ConversionException(sourceType.toString());
	}

}
