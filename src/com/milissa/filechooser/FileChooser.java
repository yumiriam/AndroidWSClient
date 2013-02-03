package com.milissa.filechooser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import com.milissa.androidwsaccess.WSCaller;
import com.milissa.bean.FileBean;

public class FileChooser extends ListActivity {

	private File currentDir;
	private FileArrayAdapter adapter;
	private WSCaller wscaller; 

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
		final Option finalOption = option;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					Toast.makeText(getApplicationContext(), "File uploading: "+ finalOption.getPath(), Toast.LENGTH_SHORT).show();

					File f = new File(finalOption.getPath());
					System.out.println("RESULT:");
					SoapSerializationEnvelope envelope = wscaller.prepareEnvelope(f);
					SoapObject result = wscaller.callWebService(envelope, WSCaller.UPLOAD_METHOD_NAME);
					
					System.out.println(result.toString());

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
		
		wscaller = new WSCaller();
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
