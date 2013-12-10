package com.example.spin;

public class SQLitem {

	private int ID;
	private String Question;
	private String Answer;
	private int Repeat;

	public SQLitem(int ID, String Question, String Answer, int Repeat) {
		super();
		this.ID = ID;
		this.Question = Question;
		this.Answer = Answer;
		this.Repeat = Repeat;
	}

	public int getID() {
		return ID;
	}

	public String getQuestion() {
		return Question;
	}

	public String getAnswer() {
		return Answer;
	}

	public int getRepeat() {
		return Repeat;
	}
	

	
}
