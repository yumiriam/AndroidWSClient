package com.milissa.androidwsaccess;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.milissa.bean.FileBean;
import com.milissa.filechooser.FileChooser;

public class MyTubeClientUI extends ActivityGroup {
	
	private TextView lblHeader;
	private TextView lblDownload;
	private TextView lblDescription;
	private EditText edtDescription;
	private TextView lblUpload;
	private TextView lblFile;
	private TextView lblFileName;


	private Button   btnDownload;
	private Button   btnUpload;
	private EditText edtDownload;
	private WSCaller wscaller;
	
	static private int FILE_CHOOSER = 42;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_tube_client_ui);
		
		wscaller = new WSCaller();
		
		lblHeader = (TextView) findViewById(R.id.textHeader);
		lblDownload = (TextView) findViewById(R.id.textDownload);
		lblUpload = (TextView) findViewById(R.id.textUpload);
		btnDownload = (Button) findViewById(R.id.buttonDownload);
		btnUpload = (Button) findViewById(R.id.buttonUpload);
		edtDownload = (EditText) findViewById(R.id.editDownloadId);
		
		lblFile = (TextView) findViewById(R.id.textFile);
		lblFileName = (TextView) findViewById(R.id.textFileName);
		lblDescription = (TextView) findViewById(R.id.textDescription);
		edtDescription = (EditText) findViewById(R.id.editDescription);
		
		lblDescription.setText("Description:");
		lblFile.setText("File name:");
	
		lblHeader.setText("MyTube 3.0");
		lblDownload.setText("Download");
		lblUpload.setText("Upload");

		btnDownload.setText("Download");
		btnUpload.setText("Click to upload a video");
		
		btnDownload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (edtDownload.getText().length() > 0) {
					SoapSerializationEnvelope envelope = wscaller.prepareEnvelope(edtDownload.getText().toString());
					SoapObject result = (SoapObject) wscaller.callWebService(envelope, WSCaller.DOWNLOAD_METHOD_NAME);
					
					if (result != null) {
		
						try {
							String name = result.getPropertyAsString("name");
							String descr = result.getPropertyAsString("description");
							
							Toast.makeText(getApplicationContext(), "Downloading file: "+name+"\n"+descr, Toast.LENGTH_SHORT).show();	
							
							FileOutputStream fos = new FileOutputStream(
									new File(Environment.getExternalStorageDirectory().getPath(), name));
							
							byte[] data = Base64.decode(result.getProperty("data").toString(), Base64.DEFAULT); 
							ByteArrayInputStream is = new ByteArrayInputStream(data);
							byte[] buffer = new byte[256];
							
							int readBytes;
							while ((readBytes = is.read(buffer)) != -1) {
								fos.write(buffer, 0, readBytes);
							}
							is.close();
							fos.flush();
							
							fos.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						
						Toast.makeText(getApplicationContext(), "Download complete!", Toast.LENGTH_SHORT).show();
						
					} else {
						Toast.makeText(getApplicationContext(), "File download error.", Toast.LENGTH_SHORT).show();						
					}
					
				}
			}
			
		});
		
		btnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FileChooser.class);
				startActivityForResult(intent, FILE_CHOOSER);
			}
			
		});
		
	}
	
    protected void onActivityResult(int requestCode, int resultCode,
            Intent data) {
        if (requestCode == FILE_CHOOSER) {
            if (resultCode == RESULT_OK) {
            	MyTubeClient app = (MyTubeClient) getApplication();
            	
            	String name = app.getChosenFile().getName();
            	String descr = edtDescription.getText().toString();

            	lblFileName.setText(name);
            	
                // cria novo objeto a ser enviado
            	FileBean uploadFile = new FileBean();
            	            	
            	uploadFile.setName(name);
            	uploadFile.setDescription(descr);

                try {
                    FileInputStream fis = new FileInputStream(app.getChosenFile());
                    byte[] b = new byte[10000];
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    // lê conteúdo do arquivo a ser enviado
                    int bytesRead;
                    while ((bytesRead = fis.read(b)) != -1)
                        bos.write(b, 0, bytesRead);

                    fis.close();
                    
                    uploadFile.setData(bos.toByteArray());

                    bos.flush();
                    bos.close();
                    
                    Toast.makeText(getApplicationContext(), "Uploading file: "+ app.getChosenFile().getName()+"\n"+descr, Toast.LENGTH_SHORT).show();
                    SoapSerializationEnvelope envelope = wscaller.prepareEnvelope(uploadFile);
                    SoapPrimitive result = (SoapPrimitive) wscaller.callWebService(envelope, WSCaller.UPLOAD_METHOD_NAME);
                    
                    if (result != null) {
                    	Toast.makeText(getApplicationContext(), "Upload complete!", Toast.LENGTH_SHORT).show();
                    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                			@Override
                			public void onClick(DialogInterface dialog, int which) {
                				switch (which){
                				case DialogInterface.BUTTON_POSITIVE:
                					//Ok button clicked
                				}
                			}
                		};

                		AlertDialog.Builder builder = new AlertDialog.Builder(this);
                		builder.setMessage("This is the ID to download your video: "+result.toString())
                			.setPositiveButton("Ok", dialogClickListener).show();
                    } else {
                    	Toast.makeText(getApplicationContext(), "File upload error.", Toast.LENGTH_SHORT).show();
                    }
	            } catch (IOException e) {
                    e.printStackTrace();
	            }
            }
        }
    }
	
	public void startChildActivity(String id, Intent intent) {
		Window window = getLocalActivityManager().startActivity(id, intent);
		if (window != null) {
			setContentView(window.getDecorView());
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first_app_ui, menu);
		return true;
	}
	
}
