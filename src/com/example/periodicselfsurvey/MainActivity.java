package com.example.periodicselfsurvey;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {

	private ListView mainListView;
	
	private ArrayList<String> surveyArrayList;
	
	boolean externalStorageUsable = false;
	
	public static final String QUESTIONS_FILE_NAME = "questions.txt";
	public static final String ANSWERS_FILE_NAME = "answers_log.txt";
	public static final int SURVEY_ACTIVITY_REQUEST_CODE = 1234;
	
	public final static String SURVEY_ACTIVITY_EXTRA_MESSAGE = "com.example.periodicselfsurvey.SURVEY_ACTIVITY_EXTRA_MESSAGE";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mainListView = (ListView) findViewById(R.id.mainSurveyList);
        
        populateSurveyList();
        
        // This is the array adapter, it takes the context of the activity as a first
        // parameter, the type of list view as a second parameter and your array as a third parameter
        ArrayAdapter<String> arrayAdapter =      
        			new ArrayAdapter<String>(this,
        								     android.R.layout.simple_list_item_1,
        								     surveyArrayList);
        
        mainListView.setAdapter(arrayAdapter);
        
        mainListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int clickedElementIndex, long l) {
            	// Launch survey Activity with proper string
            	String surveyListName = surveyArrayList.get(clickedElementIndex);
            	
            	Intent intent = new Intent(v.getContext(), SurveyActivity.class);
            	intent.putExtra(SURVEY_ACTIVITY_EXTRA_MESSAGE, surveyListName);
            	System.out.println("Starting survey "+surveyListName);
            	startActivityForResult(intent, SURVEY_ACTIVITY_REQUEST_CODE);
            }
        });

    }
    
    private void populateSurveyList(){
    	surveyArrayList = new ArrayList<String>();
    	
    	initExternalStorage();
    	
    	File baseFolder = new File(
    				this.getExternalFilesDir(null).getAbsolutePath()
    				+ File.separator);
    				// + MAIN_SURVEY_LIST_FILENAME
    	
    	if(!baseFolder.exists() || !baseFolder.isDirectory()){
    		showAlertAndExit(this, "No base folder", "Base folder does not exist or is not a folder: " + baseFolder.getAbsolutePath());
    		return;
    	}

		File[] listOfFiles = baseFolder.listFiles();
		Arrays.sort(listOfFiles);
		File surveyConfigFile;
		
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isDirectory()) {
				surveyConfigFile = new File(listOfFiles[i], QUESTIONS_FILE_NAME);
				
				if(surveyConfigFile.exists() && surveyConfigFile.isFile()) {
					surveyArrayList.add(listOfFiles[i].getName());
				}
			}
		}

    }
    
    private void initExternalStorage(){
    	String state = Environment.getExternalStorageState();

    	if (Environment.MEDIA_MOUNTED.equals(state)) {
    	    // We can read and write the media
    	    externalStorageUsable = true;
    	}  else {
    		externalStorageUsable = false;
    		showAlertAndExit(this, "No external storage",
    				"No external storage (SD card) was found, so the application can't be used.");
    		return;
    	}
    }
    
    public static void showAlertAndExit(Activity activity, String title, String message) {
    	new AlertDialog.Builder(activity)
    			.setTitle(title)
    			.setMessage(message)
		    	.show();

    	Intent intent = new Intent(Intent.ACTION_MAIN);
    	intent.addCategory(Intent.CATEGORY_HOME);
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Just adding this to support startActivityForResult
    }
}
