package br.com.lost.entities;

public class Commit extends Base{
	private String sha;
	private String url;
	private String message;
	
	private Repository repository;
	
	public Commit(String sha, long id) {
		super(id);
		this.sha = sha;
	}
	public Commit() {
		super();
	}
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Repository getRepository() {
		return repository;
	}
	public void setRepository(Repository repository) {
		this.repository = repository;
	}
}
