package com.example.spin;

public class Model {

	  private String id;
	  private String name;
	  private String questions;
	  private boolean selected;

	  public Model(String id, String name, String questions) {
		this.id=id;
	    this.name = name;
	    this.questions=questions;
	    selected = false;
	  }

	  public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
	    return name;
	  }

	  public void setName(String name) {
		this.name = name;
	  }

	  public String getQuestions() {
		return questions;
	  }

	  public void setQuestions(String questions) {
		this.questions = questions;
	  }

	  public boolean isSelected() {
	    return selected;
	  }

	  public void setSelected(boolean selected) {
	    this.selected = selected;
	  }

	} 