package com.milissa.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;

import com.milissa.androidwsaccess.MyTubeClient;
import com.milissa.androidwsaccess.R;

public class FileChooser extends ListActivity {

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
		final Option finalOption = option;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
				case DialogInterface.BUTTON_POSITIVE:
					//Yes button clicked
					File f = new File(finalOption.getPath());
					MyTubeClient app = (MyTubeClient) getApplication();
					app.setChosenFile(f);
					setResult(RESULT_OK);
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					setResult(RESULT_CANCELED);
					finish();
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
