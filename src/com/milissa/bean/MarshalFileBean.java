package com.milissa.bean;

import java.io.IOException;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class MarshalFileBean implements Marshal {

	@Override
	public Object readInstance(XmlPullParser parser, String namespace, String name,
			PropertyInfo expected) throws IOException, XmlPullParserException {
		 return android.util.Base64.decode(parser.nextText(), android.util.Base64.DEFAULT);
	}

	@Override
	public void register(SoapSerializationEnvelope cm) {
		cm.addMapping(cm.xsd, "FileBean", FileBean.class, this);		
	}

	@Override
	public void writeInstance(XmlSerializer writer, Object object)
			throws IOException {
		writer.text(android.util.Base64.encodeToString((byte[]) object, android.util.Base64.DEFAULT));
	}

}
