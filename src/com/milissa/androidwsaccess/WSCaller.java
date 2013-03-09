package com.milissa.androidwsaccess;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.milissa.bean.FileBean;

public class WSCaller {

	// NAMESPACE is targetNamespace in the WSDL.
	private static final String NAMESPACE = "http://ws.milissa.com/";
	// URL is the URL of WSDL file.
	private static final String URL = "http://192.168.1.121:8080/VideoWS/VideoService?wsdl";
	// SOAP_ACTION is NAMESPACE+METHOD_NAME
	private static final String SOAP_ACTION = "http://ws.milissa.com/";
	// METHOD_NAME is the operation name
	public static final String UPLOAD_METHOD_NAME = "upload";
	public static final String DOWNLOAD_METHOD_NAME = "download";
	
	public SoapSerializationEnvelope prepareEnvelope(FileBean fileUpload) {
		SoapObject request = new SoapObject(NAMESPACE, UPLOAD_METHOD_NAME);
		SoapSerializationEnvelope envelope = null;

		PropertyInfo pi = new PropertyInfo();
		// propertyName DEVE ser o nome do parâmetro esperado pelo WS: propOrder do Upload.java
		pi.setName("file"); 
		pi.setValue(fileUpload);
		pi.setType(FileBean.class);
		request.addProperty(pi);

		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// mapeia o tipo complexo definido pelo usuário
		envelope.addMapping(NAMESPACE, "fileBean", new FileBean().getClass());
		// serializa o array de bytes
		new MarshalBase64().register(envelope);

		envelope.encodingStyle = SoapEnvelope.ENC;
		//envelope.encodingStyle = SoapSerializationEnvelope.XSD;
		envelope.bodyOut = request;
		envelope.dotNet = false;
		envelope.setOutputSoapObject(request);
		envelope.implicitTypes= true;
		
		return envelope;
	}
	
	public SoapSerializationEnvelope prepareEnvelope(String videoId) {
		SoapObject request = new SoapObject(NAMESPACE, DOWNLOAD_METHOD_NAME);
		SoapSerializationEnvelope envelope = null;

		PropertyInfo pi = new PropertyInfo();
		// propertyName DEVE ser o nome do parâmetro esperado pelo WS: propOrder do Download.java
		pi.setName("id"); 
		pi.setValue(videoId);
		pi.setType(String.class);
		request.addProperty(pi);
	
		envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		// mapeia o tipo complexo definido pelo usuário
		envelope.addMapping(NAMESPACE, "fileBean", new FileBean().getClass());
		// serializa o array de bytes
		new MarshalBase64().register(envelope);
	
		envelope.encodingStyle = SoapEnvelope.ENC;
		envelope.bodyOut = request;
		envelope.dotNet = false;
		envelope.setOutputSoapObject(request);
		envelope.implicitTypes= true;
		
		return envelope;
	}

	public Object callWebService(SoapSerializationEnvelope envelope, String methodName) {

			HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
			try {
				androidHttpTransport.debug = true;
				androidHttpTransport.call("\""+SOAP_ACTION+methodName+"\"", envelope);
				System.out.println(androidHttpTransport.requestDump);                                           

				if (envelope.bodyIn instanceof SoapFault) {
					String faultstring = ((SoapFault) envelope.bodyIn).faultstring;
					String faultcode = ((SoapFault) envelope.bodyIn).faultcode;
					String faultactor = ((SoapFault) envelope.bodyIn).faultactor;
					StackTraceElement[] stack = ((SoapFault) envelope.bodyIn).getStackTrace();

					System.out.println("Fault:\nfaultcode: "+faultcode+"\nfaultstring: "+faultstring+"\nfaultactor: "+ faultactor);

					Exception e = new Exception();
					e.setStackTrace(stack);
					e.printStackTrace(System.err);
				} else {
					if (methodName == DOWNLOAD_METHOD_NAME)
						return (SoapObject) envelope.getResponse();
					else
						return (SoapPrimitive) envelope.getResponse();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		// fim: arquivo
		return null;
	} // end callWebService()
	
}
