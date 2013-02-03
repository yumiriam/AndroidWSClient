package com.milissa.androidwsaccess;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.milissa.filechooser.FileChooser;

public class MyTubeClientUI extends Activity {
	
	private TextView lblHeader;
	private TextView lblDownload;
	private TextView lblUpload;
	private Button   btnDownload;
	private Button   btnUpload;
	private EditText edtDownload;
	private WSCaller wscaller;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_app_ui);
		
		wscaller = new WSCaller();
		
		lblHeader = (TextView) findViewById(R.id.textHeader);
		lblDownload = (TextView) findViewById(R.id.textDownload);
		lblUpload = (TextView) findViewById(R.id.textUpload);
		btnDownload = (Button) findViewById(R.id.buttonDownload);
		btnUpload = (Button) findViewById(R.id.buttonUpload);
		edtDownload = (EditText) findViewById(R.id.editDownloadId);
	
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
					SoapObject result = wscaller.callWebService(envelope, WSCaller.DOWNLOAD_METHOD_NAME);
					
					if (result == null)
						System.out.println("Lissa, error!");
		
					try {
						FileOutputStream fos = new FileOutputStream(
								new File(Environment.getExternalStorageDirectory().getPath(), "temp.tmp"));
						
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
				}
			}
			
		});
		
		btnUpload.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), FileChooser.class);
				startActivityForResult(intent, 0);	
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first_app_ui, menu);
		return true;
	}
	
}
