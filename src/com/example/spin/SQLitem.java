package com.example.spin;

public class SQLitem {

	private String Question;
	private String Answer;
	private int Repeat;

	public SQLitem(String Question, String Answer, int Repeat) {
		super();
		this.Question = Question;
		this.Answer = Answer;
		this.Repeat = Repeat;
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
