package com.milissa.androidwsaccess;

import com.milissa.filechooser.FileChooser;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TextView;

public class FirstAppUI extends Activity {
	
	private TextView lblResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_first_app_ui);
		
		lblResult = (TextView) findViewById(R.id.result);
	
		lblResult.setText("Opening...");
		Intent intent = new Intent(getApplicationContext(), FileChooser.class);
		startActivityForResult(intent, 0);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_first_app_ui, menu);
		return true;
	}

}
