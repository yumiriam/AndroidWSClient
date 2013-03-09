package com.milissa.androidwsaccess;
import java.io.File;

import android.app.Application;

public class MyTubeClient extends Application {
	private File chosenFile;

	public File getChosenFile() {
		return chosenFile;
	}

	public void setChosenFile(File chosenFile) {
		this.chosenFile = chosenFile;
	}
	
}
