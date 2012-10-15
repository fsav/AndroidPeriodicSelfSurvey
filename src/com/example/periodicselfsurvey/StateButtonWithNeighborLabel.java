package com.example.periodicselfsurvey;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class StateButtonWithNeighborLabel extends LinearLayout {
	private Button stateButton;
	private int stateNo;
	private String[] stateLabels;
	private String neighborLabelText;
	private TextView neighborTextView;
	
	private int orientation = LinearLayout.VERTICAL; // LinearLayout.VERTICAL or LinearLayout.HORIZONTAL
	
	public StateButtonWithNeighborLabel(String[] _stateLabels, String _neighborLabelText, Context context){
		super(context);
		
		assert(_neighborLabelText != "" && _neighborLabelText != null);
		assert(_stateLabels.length > 0);
		
		stateNo = 0;
		
		stateLabels = new String[_stateLabels.length];
		for(int i=0; i<_stateLabels.length; i++){
			stateLabels[i] = _stateLabels[i];
		}
		
		neighborLabelText = _neighborLabelText;
		
		initialize();
	}
	
	public static StateButtonWithNeighborLabel createFromDescriptionString(String descriptionString, Context context){
		// Format: labeltext:state1:state2:...
		String[] fields = descriptionString.split(":");
		
		assert(fields.length >= 3);
		
		int numNonEmptyStates = 0;
		for(int i=1; i<fields.length; i++){
			if(fields[i] != null && fields[i] != "" && fields[i] != " "){
				numNonEmptyStates += 1;
			}
		}
		
		String label = fields[0];
		String[] states = new String[numNonEmptyStates];
		int nonEmptyPos = 0;
		for(int i=1; i<fields.length; i++){
			if(fields[i] != null && fields[i] != "" && fields[i] != " "){
				states[nonEmptyPos] = fields[i];
				nonEmptyPos += 1;
			}
		}
		
		return new StateButtonWithNeighborLabel(states, label, context);
	}
	
	public String getAnswerLinePart() {
		return neighborLabelText + "\t" + stateLabels[stateNo] + "\t";
	}
	
	private void initialize() {
		// remember this class extends LinearLayout
		setOrientation(orientation);
		// WRAP_CONTENT : just large enough to fits its content
		setLayoutParams(
	    		new LinearLayout.LayoutParams(
	    				LinearLayout.LayoutParams.WRAP_CONTENT , 
	    				LinearLayout.LayoutParams.WRAP_CONTENT ));

		addNeighborLabelTextView();
		addStateButton();
	}
	
	private void addNeighborLabelTextView() {
		neighborTextView = new TextView(this.getContext());
		neighborTextView.setLayoutParams(
        		new ViewGroup.LayoutParams(
        				ViewGroup.LayoutParams.WRAP_CONTENT,
        				ViewGroup.LayoutParams.WRAP_CONTENT) );
		neighborTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		neighborTextView.setText("   " + neighborLabelText + "   ");
		neighborTextView.setPadding(3,3,3,3);

		addView(neighborTextView);
	}
	
	private void toggleNextState(){
		stateNo = (stateNo + 1) % stateLabels.length;
		stateButton.setText(stateLabels[stateNo]);
		System.out.println("Setting state to " + stateLabels[stateNo] + "= stateno " + Integer.toString(stateNo));
	}
	
    private void addStateButton(){
    	stateButton = new Button(this.getContext());
    	stateButton.setText(stateLabels[stateNo]);
    	stateButton.setPadding(5,5,5,5);
    	
    	stateButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				toggleNextState();
			}
		});

    	addView(stateButton);

    	// For some reason can't use margins here as it squeezes the button... probably has to do with the FlowLayout class
		//LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)stateButton.getLayoutParams();
		//params.setMargins(6,6,6,6);
    }
}
