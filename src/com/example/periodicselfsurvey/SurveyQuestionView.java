package com.example.periodicselfsurvey;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class SurveyQuestionView extends LinearLayout {
	private SurveyQuestion questionObj;
	private View answerView;
	private TextView questionTextView;
	
	private boolean isActive;
	
	public SurveyQuestionView(SurveyQuestion _questionObj, Context context) {
		super(context);
		
		isActive = true;
		
		questionObj = _questionObj;
		
		initialize();
	}
	
	public boolean isActive(){
		return isActive;
	}
	
	public String getAnswerLine(boolean repeatQuestion, boolean repeatAnswerString) {
		Date date = new Date();
		String line = (new SimpleDateFormat("yyyy-MM-dd\thh:mm:ss\t")).format(date);
		
		line += questionObj.getQuestionId() + "\t";
		
		if(repeatQuestion) {
			line += questionObj.getQuestionString() + "\t";
		}
		
		switch(questionObj.getType()) {
		case YES_NO:
			CheckBox cb = (CheckBox)answerView;
			if(cb.isChecked()){
				line += "1";
				if(repeatAnswerString){ line += "\tYES"; }
			}else{
				line += "0";
				if(repeatAnswerString){ line += "\tNO"; }
			}
			break;
			
		case MULTIPLE_CHOICES:
			RadioGroup rg = (RadioGroup)answerView;
			line += Integer.toString(rg.getCheckedRadioButtonId());
			if(repeatAnswerString && rg.getCheckedRadioButtonId() >= 0){
				line += "\t" + questionObj.getChoices()[rg.getCheckedRadioButtonId()];
			}
			break;
			
		case TEXT_ANSWER:
			EditText et = (EditText)answerView;
			line += et.getText();
			break;
			
		case STATE_BUTTON_ARRAY:
			// TODO
			FlowLayout buttonArrayViewGroup = (FlowLayout)answerView;
			for(int i=0; i<buttonArrayViewGroup.getChildCount(); i++){
				StateButtonWithNeighborLabel b = (StateButtonWithNeighborLabel)buttonArrayViewGroup.getChildAt(i);
				line += b.getAnswerLinePart();
			}
			break;
		}
		
		return line;
	}
	
	private void initialize() {
		// remember this class extends LinearLayout
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(
	    		new LinearLayout.LayoutParams(
	    				LinearLayout.LayoutParams.FILL_PARENT, 
	    				LinearLayout.LayoutParams.FILL_PARENT));

		addQuestionTextView(questionObj.getQuestionString());
		
		switch(questionObj.getType()) {
		case YES_NO:
			initializeYesNoQuestionView(questionObj);
			break;
		case MULTIPLE_CHOICES:
			initializeMultipleChoicesQuestionView(questionObj);
			break;
		case TEXT_ANSWER:
			initializeTextAnswerQuestionView(questionObj);
			break;
		case STATE_BUTTON_ARRAY:
			initializeStateButtonArrayQuestionView(questionObj);
		}
	}
	
	private void addQuestionTextView(String question) {
        questionTextView = new TextView(this.getContext());
        questionTextView.setLayoutParams(
        		new ViewGroup.LayoutParams(
        				ViewGroup.LayoutParams.FILL_PARENT,
        				ViewGroup.LayoutParams.WRAP_CONTENT) );
        questionTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        questionTextView.setText(question);
        questionTextView.setPadding(5,5,5,5);
        questionTextView.setBackgroundColor(Color.LTGRAY);
        
        questionTextView.setClickable(true);
        questionTextView.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				toggleQuestionOnOff();
			}
		});
        
        addView(questionTextView);
	}
	
	public void toggleQuestionOnOff(){
		if(isActive) {
			answerView.setVisibility(GONE);
			questionTextView.setTextColor(Color.GRAY);
			isActive = false;
		} else {
			answerView.setVisibility(VISIBLE);
			questionTextView.setTextColor(Color.BLACK);
			isActive = true;
		}
	}
	
	private void initializeMultipleChoicesQuestionView(SurveyQuestion questionObj){
	    final RadioButton[] rb = new RadioButton[questionObj.getChoices().length];
	    RadioGroup rg = new RadioGroup(this.getContext());
	    rg.setOrientation(RadioGroup.VERTICAL);
	    for(int i=0; i<questionObj.getChoices().length; i++){
	        rb[i]  = new RadioButton(this.getContext());
	        rb[i].setText(questionObj.getChoices()[i]);
	        rb[i].setId(i); // to make it easier to relate checked button to array position
	        rg.addView(rb[i]);
	    }
	    addView(rg);
		
	    // Keep a pointer to the radio group directly, makes form handling easier
		answerView = rg;
	}
	
	private void initializeYesNoQuestionView(SurveyQuestion questionObj){
        CheckBox cb = new CheckBox(this.getContext());
        addView(cb);
        
        answerView = cb;
	}
	
	private void initializeTextAnswerQuestionView(SurveyQuestion questionObj){
		EditText editText = new EditText(this.getContext());
		addView(editText);
		
		// Need to add _before_ setting margin, for some reason...
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)editText.getLayoutParams();
		params.setMargins(5,5,5,5);

		answerView = editText;
	}
	
	private void initializeStateButtonArrayQuestionView(SurveyQuestion questionObj){
		FlowLayout wrappingLayout = new FlowLayout(this.getContext()); 
		
		String[] choices = questionObj.getChoices();
		
	    for(int i=0; i<choices.length; i++){
	    	StateButtonWithNeighborLabel sb =
	    				StateButtonWithNeighborLabel.createFromDescriptionString(choices[i], this.getContext());
	    	wrappingLayout.addView(sb);
	    }
	    
	    answerView = wrappingLayout;
	    
	    addView(wrappingLayout);
	}
}
