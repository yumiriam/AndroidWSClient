package com.milissa.filechooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.milissa.androidwsaccess.R;
import com.milissa.bean.FileBean;

public class FileChooser extends ListActivity {

	// NAMESPACE is targetNamespace in the WSDL.
	private static final String NAMESPACE = "http://ws.milissa.com/";
	// URL is the URL of WSDL file.
	private static final String URL = "http://192.168.0.14:8081/TesteWS/TestService?wsdl";
	// SOAP_ACTION is NAMESPACE+METHOD_NAME
	private static final String SOAP_ACTION = "http://ws.milissa.com/upload";
	// METHOD_NAME is the operation name
	private static final String METHOD_NAME = "upload";

	private File currentDir;
	private FileArrayAdapter adapter;

	private void fill(File file) {
		File[]dirs = file.listFiles();

		this.setTitle("Current Dir: "+file.getName());

		List<Option>dir = new ArrayList<Option>();
		List<Option>fls = new ArrayList<Option>();
		try {
			for(File ff: dirs) {
				if(ff.isDirectory()){
					dir.add(new Option(ff.getName(),"Folder",ff.getAbsolutePath()));
				} else {
					fls.add(new Option(ff.getName(),"File Size: "+ff.length(),ff.getAbsolutePath()));
				}
			}
		} catch(Exception e) {

		}
		Collections.sort(dir);
		Collections.sort(fls);
		dir.addAll(fls);
		if(!file.getName().equalsIgnoreCase("sdcard"))
			dir.add(0,new Option("..","Parent Directory",file.getParent()));

		adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
		this.setListAdapter(adapter);
	}

	private void onFileClick(Option option) {
		Toast.makeText(this, "File uploading: "+ option.getPath(), Toast.LENGTH_SHORT).show();

		final Option finalOption = option;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

					// inicio: arquivo
					finalOption.getPath();

					// pega informações do arquivo a ser enviado
					File f = new File(finalOption.getPath());
					String fullName = finalOption.getName();
					int dotIndex = fullName.lastIndexOf('.');
					String name = fullName.substring(0, dotIndex);
					String type = fullName.substring(dotIndex+1, fullName.length());

					try {
						FileInputStream fis = new FileInputStream(f);
						int length = fis.available();
						Toast.makeText(getApplicationContext(), "File size: "+ length, Toast.LENGTH_SHORT).show();
						byte[] b = new byte[10000];
						ByteArrayBuffer byteArray = new ByteArrayBuffer(10000);
						
						// lê conteúdo do arquivo a ser enviado
						int bytesRead;
						while ((bytesRead = fis.read(b)) != -1) {
							byteArray.append(b, 0, bytesRead);
						}

						// cria novo objeto a ser enviado
						FileBean file = new FileBean();
						file.setData(byteArray.toByteArray());
						file.setName(name);
						file.setType(type);

						PropertyInfo pi = new PropertyInfo();
						// propertyName DEVE ser o nome do parâmetro esperado pelo WS: propOrder do Upload.java
						pi.setName("file"); 
						pi.setValue(file);
						pi.setType(FileBean.class);
						request.addProperty(pi);

						SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
						// mapeia o tipo complexo definido pelo usuário
						envelope.addMapping(NAMESPACE, "fileBean", new FileBean().getClass());
						// serializa o array de bytes
						new MarshalBase64().register(envelope);

						envelope.encodingStyle = SoapEnvelope.ENC;
						envelope.bodyOut = request;
						envelope.dotNet = false;
						envelope.setOutputSoapObject(request);
						envelope.implicitTypes= true;

						HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);
						try {
							androidHttpTransport.debug = true;
							androidHttpTransport.call(SOAP_ACTION, envelope);
							System.out.println(androidHttpTransport.requestDump);		    				
							
						    if (envelope.bodyIn instanceof SoapFault) {
						        String faultstring = ((SoapFault) envelope.bodyIn).faultstring;
						        String faultcode = ((SoapFault) envelope.bodyIn).faultcode;
						        String faultactor = ((SoapFault) envelope.bodyIn).faultactor;
						        StackTraceElement[] stack = ((SoapFault) envelope.bodyIn).getStackTrace();
						        
						        System.out.println("Fault:");
						        System.out.println("faultcode: "+ faultcode);
						        System.out.println("faultstring: "+ faultstring);
						        System.out.println("faultactor: "+ faultactor);
						        
						        Exception e = new Exception();
						        e.setStackTrace(stack);
						        e.printStackTrace(System.err);
						    } else {
						        SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
						        System.out.println(String.valueOf(resultsRequestSOAP));
								if (resultsRequestSOAP != null)
									Toast.makeText(getApplicationContext(), "File uploaded!", Toast.LENGTH_SHORT).show();
								else
									Toast.makeText(getApplicationContext(), "An error has ocurred!", Toast.LENGTH_SHORT).show();
						    }
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}		
					// fim: arquivo
					
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					//No button clicked
					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Selected file:\n"+ option.getName() +"\nIs this the file to upload?").setPositiveButton("Yes", dialogClickListener)
		.setNegativeButton("No", dialogClickListener).show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_chooser);

		// inicia o diretório corrente no cartão SD
		currentDir = new File(Environment.getExternalStorageDirectory().getPath());
		fill(currentDir);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_file_chooser, menu);
		return true;
	}

	@Override
	protected void onListItemClick(ListView list, View view, int position, long id) {
		super.onListItemClick(list, view, position, id);
		Option o = adapter.getItem(position);
		if (o.getData().equalsIgnoreCase("folder")||o.getData().equalsIgnoreCase("parent directory")) {
			currentDir = new File(o.getPath());
			fill(currentDir);
		} else {
			onFileClick(o);
		}
	}

}
