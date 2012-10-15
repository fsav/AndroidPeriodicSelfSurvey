package com.example.periodicselfsurvey;

import java.util.ArrayList;

public class SurveyQuestion {

	public enum QuestionType { YES_NO, MULTIPLE_CHOICES, TEXT_ANSWER, STATE_BUTTON_ARRAY }
	
	private String questionId;
	private QuestionType type;
	private String questionString;
	// Only for MULTIPLE_CHOICES and STATE_BUTTON_ARRAY
	private String[] choices;
	
	public SurveyQuestion(String _questionId, QuestionType _type, String _questionString, String[] _choices) {
		questionId = _questionId;
		type = _type;
		questionString = _questionString;
		
		if(_choices != null) {
			if(_type != QuestionType.MULTIPLE_CHOICES && _type != QuestionType.STATE_BUTTON_ARRAY) {
				throw new IllegalArgumentException("'choices' can only be set for MULTIPLE_CHOICES or STATE_BUTTON_ARRAY type of question");
			}
		
			choices = new String[_choices.length];
			for(int i=0; i < _choices.length; i++){
				choices[i] = _choices[i];
			}
		}else{
			choices = null;
		}
	}
	
	public String getQuestionId() {
		return questionId;
	}
	
	public QuestionType getType() {
		return type;
	}
	
	public String getQuestionString() {
		return questionString;
	}

    // returns a copy
    public String[] getChoices() {
		String[] returned = new String[choices.length];
		for(int i=0; i < choices.length; i++){
			returned[i] = choices[i];
		}
		return returned;
    }
	
	public String toString() {
		int choicesLength = -1;
		if(choices != null){
			choicesLength = choices.length;
		}
		return "QuestionStruct: "
					+ "id=" + questionId
					+ ", type=" + type.toString()
					+ ", questionString=" + questionString
					+ ", choices length=" + choicesLength;
	}
	
	public static SurveyQuestion createFromQuestionLine(String questionLine) throws IllegalArgumentException {
		String[] fields = questionLine.split("\t");
		
		if(fields.length < 3) {
			throw new IllegalArgumentException("Invalid question format: less than 3 fields");
		}
		
		String questionId = fields[0];
		
		String questionString = fields[1];
		
		if(questionString.length() == 0) {
			throw new IllegalArgumentException("Invalid question format: no question");
		}
		
		QuestionType type;
		
		try {
			type = QuestionType.valueOf(fields[2]);
		} catch(IllegalArgumentException e) {
			throw new IllegalArgumentException("Invalid question format: bad question type: " + fields[2]);
		}

		switch(type) {
		case YES_NO: case TEXT_ANSWER:
			// for both these cases our job is done
			if(fields.length > 3){
				throw new IllegalArgumentException("Invalid question format: more than 2 tab-separated fields: "+questionLine);
			}
			return new SurveyQuestion(questionId, type, questionString, null);
			
		case MULTIPLE_CHOICES:
			// rest of fields are answer labels
			if(fields.length < 5) {
				throw new IllegalArgumentException("Invalid question format: less than 2 choices for MULTIPLE_CHOICES: "+questionLine);
			}
			
			// NOTE: no 'break' here, MULTIPLE_CHOICES handling continues below
			
		case STATE_BUTTON_ARRAY:
			ArrayList<String> tmp = new ArrayList<String>();
			
			for(int i=0; i<fields.length-3; i++){
				tmp.add(fields[i+3]);
			}
			
			return new SurveyQuestion(questionId, type, questionString, tmp.toArray(new String[tmp.size()]));
		}
		
		throw new RuntimeException("This point should not be reached");
	}
}
