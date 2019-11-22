package br.com.lost.util;

public class Constantes {
	//GITHUB API
	public static String URL_BASE = "https://api.github.com";
	
	public static String SEARCH_REPOS = URL_BASE + "/search/repositories";
	public static String QUERY_REPOS = "?q=language:<language>&sort=stars&order=desc&per_page=100";
	
	public static String LIST_ISSUE_EVENTS = URL_BASE + "/repos/<owner>/<repo>/issues/events?page=<page>&per_page=100";
	public static String COUNT_COMMITS = URL_BASE + "/repos/<owner>/<repo>/commits?per_page=1";
	public static String LIST_COMMITS = URL_BASE + "/repos/<owner>/<repo>/commits?page=<page>&per_page=100";
	public static String LIST_FILES_COMMIT = URL_BASE + "/repos/<owner>/<repo>/commits/<sha>";
	
	//DATABASE
	public static String DATABASE_URL = "jdbc:mysql://<ENDERECO_BANCO>?autoReconnect=true&useSSL=false&rewriteBatchedStatements=true&useUnicode=true&characterEncoding=utf-8";
	public static String DATABASE_USER = "<USUARIO_BANCO>";
	public static String DATABASE_PASSWORD = "<SENHA_BANCO>";

	//GITHUB API - TOKENS
	public static String tokens[] = {"<LISTA DE TOKENS DO GITHUB>"}; 
}
