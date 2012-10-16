AndroidPeriodicSelfSurvey
=========================

This little Android app allows one to create surveys using a custom text file format for defining them, and logging the answers to another flat-file like format. The only GUI elements are the surveys themselves; the survey creation can only be done by editing the files, and survey collection & graphs must be done by analyzing the dumped data with other tools (scripts, etc.).

This is mostly geared towards programmers, or, more reastically, me (at least for the moment) :) Yet I thought some might like to use this as a starting point for similar apps, hence I'm publishing it. My original intention was to use it for "daily self surveys" (for Self Quantification), hence the name "periodic self survey".

To create a new survey, you create a new directory under your smartphone's internal storage (ie. the files you seed when you connect your device with an USB cable to your computer). You create it under Android/data/com.example.periodicselfsurvey/files (I need to rename that 'example' part). The name of the directory is the name of your survey.

In that directory, you create a file named survey.txt, and each line will be a question.Common fields are ID, a unique integer identifier for your question (unique in this survey), TITLE, free text for the question itself, and QUESTION_TYPE, which must be either YES_NO, MULTIPLE_CHOICES, TEXT_ANSWER or STATE_BUTTON_ARRAY.

Fields are tab-separated.

Depending on the type of question, the format will be:

- For a checkbox:
ID	TITLE	YES_NO

E.g.
123	My yes-no question.	YES_NO

- For a multiple choice:
ID	TITLE	MULTIPLE_CHOICES	ANSWER_0	ANSWER_1	...

- For a text answer:
ID	TITLE	TEXT_ANSWER

- For a state button array (a series of buttons, each with a title, and a few short answers through which you can cycle):

ID	TITLE	STATE_BUTTON_ARRAY	TITLE_BUTTON_1:C1_1:C1_2:C1_3:...	TITLE_BUTTON_2:C2_1:C2_2:C2_3	...

e.g.
8	Time slept per hour	STATE_BUTTON_ARRAY	21:0:60:45:30:15	22:0:60:45:30:15	23:60:45:30:15:0	0:60:45:30:15:0	1:60:45:30:15:0	2:60:45:30:15:0	3:60:45:30:15:0	4:60:45:30:15:0	5:60:45:30:15:0	6:60:45:30:15:0	7:60:45:30:15:0	8:0:60:45:30:15	9:0:60:45:30:15	10:0:60:45:30:15	11:0:60:45:30:15

Answers are logged one per line in a file in the survey directory named answers.txt. You can activate/deactivate logging for each question when answering by tapping on the question bar. The format is to be described later, but it varies by question type and includes a timestamp, the ID, and the value of the answer, along with an ID for each answer to make it easier to do analyses with scripts.

