package br.com.lost.entities;

public class Base {
	private long id;

	public Base() {
		super();
	}
	
	public Base(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
