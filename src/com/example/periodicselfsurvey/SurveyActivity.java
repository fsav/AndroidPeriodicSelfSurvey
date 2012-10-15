package com.example.periodicselfsurvey;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class SurveyActivity extends Activity {
	
	private String surveyName;
	private LinearLayout questionsListLayout;
	private ArrayList<SurveyQuestion> questionObjList;
	private ArrayList<SurveyQuestionView> questionViewList;
	private Button saveButton;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        
        Intent intent = getIntent();
        surveyName = intent.getStringExtra(MainActivity.SURVEY_ACTIVITY_EXTRA_MESSAGE);
        
        setTitle(surveyName);
        
        questionsListLayout = (LinearLayout)findViewById(R.id.questionsListLayout);
        
        readQuestionList();
        
        questionViewList = new ArrayList<SurveyQuestionView>();
        
        constructSurveyViews();
        
        addSaveButton();
    }
    
    private void readQuestionList() {
    	// No need to do this, as the MainActivity should already have checked
    	// that external storage is accessible
    	// initExternalStorage();

    	File questionsFile = new File(
    				this.getExternalFilesDir(null).getAbsolutePath()
    				+ File.separator
    				+ surveyName
    				+ File.separator
    				+ MainActivity.QUESTIONS_FILE_NAME);
    	
    	// This would be weird, as MainActivity already checked, but anyway...
    	if(!questionsFile.exists() || !questionsFile.isFile()){
    		MainActivity.showAlertAndExit(this, "No questions file", "No questions file at: " + questionsFile.getAbsolutePath());
    		return;
    	}
    	
    	InputStream fis;
    	BufferedReader br;
    	String line;
    	
    	ArrayList<String> keptLines = new ArrayList<String>();
    	
    	try {
	    	fis = new FileInputStream(questionsFile);
	    	br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
	    	while ((line = br.readLine()) != null) {
	    		// Just checking that the line is not simply EOL characters
	    		if(line.length() > 5) {
	    			keptLines.add(line);
	    			System.out.println("Read line "+line);
	    		}
	    	}
	    	br.close();
	    } catch(FileNotFoundException e) {
			MainActivity.showAlertAndExit(
					this,
					"No question file found",
					"No file containing question list found at " + questionsFile.getAbsolutePath());
			return;
		} catch(IOException e) {
			MainActivity.showAlertAndExit(
					this,
					"IO error",
					"IO error while reading from " + questionsFile.getAbsolutePath());
			return;
		}
    	
    	if(keptLines.size() == 0){
			MainActivity.showAlertAndExit(
					this,
					"Empty questions file",
					"File contains no usable lines: " + questionsFile.getAbsolutePath());
			return;
    	}
    	
        questionObjList = new ArrayList<SurveyQuestion>();
        for(String qstr : keptLines) {
        	try {
        		questionObjList.add(SurveyQuestion.createFromQuestionLine(qstr));
        	} catch(IllegalArgumentException e) {
    			MainActivity.showAlertAndExit(
    					this,
    					"Could not parse question",
    					"Could not parse question line: " + qstr);
    			return;
        	}
        }
    }
    
	private void addQuestionSeparator(){
		View v = new View(this);
		v.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, 1));
		v.setBackgroundColor(Color.BLACK);
		questionsListLayout.addView(v);
	}
    
    private void constructSurveyViews(){
    	for(SurveyQuestion qstruct : questionObjList) {
    		SurveyQuestionView sqv = new SurveyQuestionView(qstruct, this);
    		questionViewList.add(sqv);
    		questionsListLayout.addView(sqv);
    		addQuestionSeparator();
    	}
    }
    
    private int saveAnswers(){
		File answersFile = new File(
				this.getExternalFilesDir(null).getAbsolutePath()
				+ File.separator
				+ surveyName
				+ File.separator
				+ MainActivity.ANSWERS_FILE_NAME);

		BufferedWriter bufferWriter;
		int numAnswers = 0;
		try{
			if(!answersFile.exists()){
				answersFile.createNewFile();
			}
			
			//true = append file
			FileWriter fileWriter = new FileWriter(answersFile, true);
	        bufferWriter = new BufferedWriter(fileWriter);
	        
	        for(SurveyQuestionView sqv : questionViewList){
	        	if(sqv.isActive()){
	        		numAnswers += 1;
	        		bufferWriter.write(sqv.getAnswerLine(false, true) + "\r\n");
	        	}
	        }
	        
	        bufferWriter.close();
		}catch(IOException e){
			MainActivity.showAlertAndExit(
					this,
					"IOException while writing",
					"IOException while writing to answers file: " + e.toString());
			return -1;
		}
        
        return numAnswers;
    }
    
    private void saveAnswersAndGoBackToMainMenu(){
    	int numAnswers = saveAnswers();
    	new AlertDialog.Builder(this)
				.setTitle("Answers saved")
				.setMessage(Integer.toString(numAnswers) + " answers saved to log file.")
		        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   setResult(MainActivity.SURVEY_ACTIVITY_REQUEST_CODE, new Intent());
		        	   finish();
		           }
		        })
		    	.show();
    }
    
    private void addSaveButton(){
    	saveButton = new Button(this);
    	saveButton.setText("Log answers");
    	saveButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				saveAnswersAndGoBackToMainMenu();
			}
		});
    	questionsListLayout.addView(saveButton);
    	
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)saveButton.getLayoutParams();
		params.setMargins(10,10,10,10);
    }
}
