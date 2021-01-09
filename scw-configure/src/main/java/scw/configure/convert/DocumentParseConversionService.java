package scw.configure.convert;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import scw.convert.ConversionException;
import scw.convert.ConversionService;
import scw.convert.TypeDescriptor;
import scw.dom.DomBuilder;
import scw.dom.DomUtils;
import scw.io.Resource;

public class DocumentParseConversionService implements ConversionService{
	private final DomBuilder domBuilder;
	
	public DocumentParseConversionService(){
		this(DomUtils.getDomBuilder());
	}
	
	public DocumentParseConversionService(DomBuilder domBuilder){
		this.domBuilder = domBuilder;
	}
	
	public boolean isSupported(TypeDescriptor sourceType,
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
			return domBuilder.parse((InputStream)source);
		}else if(Reader.class.isAssignableFrom(sourceType.getType())){
			return domBuilder.parse((Reader)source);
		}else if(String.class.isAssignableFrom(sourceType.getType())){
			return domBuilder.parse((String)source);
		}else if(InputSource.class.isAssignableFrom(sourceType.getType())){
			return domBuilder.parse((InputSource)source);
		}else if(File.class.isAssignableFrom(sourceType.getType())){
			return domBuilder.parse((File)source);
		}else if(Resource.class.isAssignableFrom(sourceType.getType())){
			return domBuilder.parse((Resource)source);
		}
		throw new ConversionException(sourceType.toString());
	}

}
