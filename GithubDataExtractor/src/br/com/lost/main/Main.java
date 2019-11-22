package br.com.lost.main;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.BatchUpdateException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.lost.dao.CommitDAO;
import br.com.lost.dao.IssueDAO;
import br.com.lost.dao.IssueEventDAO;
import br.com.lost.dao.RepositoryDAO;
import br.com.lost.entities.Commit;
import br.com.lost.entities.Issue;
import br.com.lost.entities.IssueEvent;
import br.com.lost.entities.Repository;
import br.com.lost.util.Constantes;

public class Main {
	private static int responseCode;
	private static int tokenIndex = 0;
	private static String TOKEN;

	public static void main(String[] args) {
		ArrayList<Repository> repos = new ArrayList<Repository>();
		TOKEN = Constantes.tokens[tokenIndex];
		try {
			ArrayList<String> languages = new ArrayList<String>();

			languages.add("java");
			languages.add("python");
			languages.add("ruby");
			languages.add("php");
			languages.add("javascript");
			languages.add("cpp");

			for (String lang : languages) {
				System.out.println("Language: " + lang);
				repos = listRepos(lang);
				
				listIssuesCommits(repos);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Repository> listRepos(String lang) throws IOException, JSONException{
		ArrayList<Repository> repos = new ArrayList<>();

		String url = Constantes.SEARCH_REPOS + Constantes.QUERY_REPOS.replace("<language>", lang);

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		System.out.println(url);
		do {
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("Authorization", "token " + TOKEN);
			con.setDoOutput(true);
			responseCode = con.getResponseCode();

			if(responseCode != HttpURLConnection.HTTP_OK) {
				if(tokenIndex + 1 == Constantes.tokens.length) {
					tokenIndex = 0;
				}
				TOKEN = Constantes.tokens[tokenIndex++];
			}
		} while(responseCode != HttpURLConnection.HTTP_OK);

		responseCode = con.getResponseCode();
		System.out.println("#LIST REPOSITORIES");

		if(responseCode == HttpURLConnection.HTTP_OK){
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer resp = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				resp.append(inputLine);
			}
			in.close();

			JSONObject jsonRepos = new JSONObject(resp.toString());
			JSONArray jsonArray = jsonRepos.getJSONArray("items");
			System.out.println("----------------");
			JSONObject json = null;
			Repository repo = null;
			for (int i = 0; i < jsonArray.length(); i++) {
				repo = new Repository();
				json = (JSONObject) jsonArray.get(i);

				repo.setId(json.getInt("id"));
				repo.setName(json.getString("name"));
				repo.setOwner(json.getJSONObject("owner").getString("login"));
				repo.setLanguage(json.getString("language"));
				repo.setUrl(json.getString("url"));
				repos.add(repo);
			}

			RepositoryDAO repositoryDAO = new RepositoryDAO();
			try {
				for (Repository repository : repos) {
					repositoryDAO.insert(repository);
				}
			} catch(SQLIntegrityConstraintViolationException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return repos;
	}

	public static void listIssuesCommits(ArrayList<Repository> repos) throws IOException, JSONException{
		System.out.println("# Repository");
		for (Repository repository : repos) {
			System.out.println(repository.getName());
			
			countCommits(repository);
			listIssueEvent(repository);
			listCommits(repository);			
		}
	}

	public static int listIssueEvent(Repository repo) throws IOException, JSONException{	
		String url;
		int count = 0, numPages = 1, page = 1;
		String lastPageLink;
		ArrayList<IssueEvent> issueEvents = null;
		ArrayList<Issue> issues = null;
		issues = new ArrayList<Issue>();
		issueEvents = new ArrayList<IssueEvent>();

		URL obj;
		HttpURLConnection con;
		do {
			url = Constantes.LIST_ISSUE_EVENTS.replace("<owner>", repo.getOwner()).replace("<repo>", repo.getName()).replace("<page>", String.valueOf(page));
			obj = new URL(url);
			do {
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("content-type", "application/json");
				con.setRequestProperty("Authorization", "token " + TOKEN);
				con.setDoOutput(true);
				responseCode = con.getResponseCode();

				if(responseCode != HttpURLConnection.HTTP_OK) {
					if(tokenIndex + 1 == Constantes.tokens.length) {
						break;
					}
					TOKEN = Constantes.tokens[tokenIndex++];
				}
			} while(responseCode != HttpURLConnection.HTTP_OK);

			if(responseCode == HttpURLConnection.HTTP_OK){			
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer resp = new StringBuffer();

				Map<String, List<String>> map = con.getHeaderFields();

				List<String> links = map.get("Link");

				if(links != null) {
					lastPageLink = links.toString().substring(links.toString().indexOf("<", links.toString().indexOf("<") + 1) + 1, links.toString().indexOf(">", links.toString().indexOf(">") + 1));
					numPages = Integer.parseInt(lastPageLink.substring(lastPageLink.indexOf("?page="), lastPageLink.indexOf("&")).replace("?page=", ""));
				}

				while ((inputLine = in.readLine()) != null) {
					resp.append(inputLine);
				}
				in.close();

				JSONArray jsonArray = new JSONArray(resp.toString());
				JSONObject json = null;
				Issue issue = null;
				IssueEvent issueEvent = null;

				for (int i = 0; i < jsonArray.length(); i++) {
					json = (JSONObject) jsonArray.get(i);
					if(!json.get("commit_id").toString().equals("null")) {
						if(json.get("issue") != null && !json.get("issue").toString().equals("null")) {
							issue = new Issue();
							issue.setId(json.getJSONObject("issue").getLong("id"));
							issue.setState(json.getJSONObject("issue").getString("state"));
							issue.setTitle(json.getJSONObject("issue").get("title").toString().equals("null") ? "" : json.getJSONObject("issue").getString("title"));
							issue.setBody(json.getJSONObject("issue").get("body").toString().equals("null") ? "": json.getJSONObject("issue").getString("body"));
							issue.setUrl(json.getJSONObject("issue").getString("url"));
							issue.setRepository(repo);

							issues.add(issue);
						
							issueEvent = new IssueEvent();

							issueEvent.setId(json.getLong("id"));
							issueEvent.setCommitId(json.getString("commit_id"));
							issueEvent.setUrl(json.getString("url"));
							issueEvent.setIssue(issue);
							issueEvent.setRepository(repo);

							issueEvents.add(issueEvent);
							count++;
						}
					}
				}

				IssueDAO issueDAO = new IssueDAO();
				try {
					if(page >= numPages) {						
						issueDAO.insertBatch(issues);

						issues = new ArrayList<Issue>();
					}
				} catch(BatchUpdateException e) {
					e.printStackTrace();
				} catch(SQLIntegrityConstraintViolationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}

				IssueEventDAO issueEventDAO = new IssueEventDAO();
				try {
					if(page >= numPages) {						
						issueEventDAO.insertBatch(issueEvents);

						issueEvents = new ArrayList<IssueEvent>();
					}
				} catch(BatchUpdateException e) {
					e.printStackTrace();
				} catch(SQLIntegrityConstraintViolationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(responseCode);
			}
			page++;
		}while(page <= numPages);
		return count;
	}

	public static int countCommits(Repository repo) throws IOException, JSONException{	
		String url = Constantes.COUNT_COMMITS.replace("<owner>", repo.getOwner()).replace("<repo>", repo.getName());
		int count = 0;
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		do {
			con.setRequestMethod("GET");
			con.setRequestProperty("content-type", "application/json");
			con.setRequestProperty("Authorization", "token " + TOKEN);
			con.setDoOutput(true);
			responseCode = con.getResponseCode();

			if(responseCode != HttpURLConnection.HTTP_OK) {
				if(tokenIndex + 1 == Constantes.tokens.length) {
					break;
				}
				TOKEN = Constantes.tokens[tokenIndex++];
			}
		} while(responseCode != HttpURLConnection.HTTP_OK);

		if(responseCode == HttpURLConnection.HTTP_OK){
			Map<String, List<String>> map = con.getHeaderFields();

			List<String> links = map.get("Link");

			count = Integer.parseInt(links.toString().substring(links.toString().indexOf("&page=", links.toString().indexOf("&page=") + 1), links.toString().lastIndexOf(">")).replace("&page=", ""));
		} else {
			System.out.println(responseCode);
		}
		return count;
	}

	public static int listCommits(Repository repo) throws IOException, JSONException{	
		int numPages = 1, page = 1, count = 0;
		String url;
		URL obj;
		HttpURLConnection con;
		String lastPageLink;
		ArrayList<Commit> commits = null;
		commits = new ArrayList<Commit>();

		do {
			url = Constantes.LIST_COMMITS.replace("<owner>", repo.getOwner()).replace("<repo>", repo.getName()).replace("<page>", String.valueOf(page));
			obj = new URL(url);
			do {
				con = (HttpURLConnection) obj.openConnection();
				con.setRequestMethod("GET");
				con.setRequestProperty("content-type", "application/json");
				con.setRequestProperty("Authorization", "token " + TOKEN);
				con.setDoOutput(true);
				responseCode = con.getResponseCode();

				if(responseCode != HttpURLConnection.HTTP_OK) {
					if(tokenIndex + 1 == Constantes.tokens.length) {
						break;
					}
					TOKEN = Constantes.tokens[++tokenIndex];
				}
			} while(responseCode != HttpURLConnection.HTTP_OK);

			if(responseCode == HttpURLConnection.HTTP_OK){			
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				StringBuffer resp = new StringBuffer();

				Map<String, List<String>> map = con.getHeaderFields();

				List<String> links = map.get("Link");

				if(links != null) {
					lastPageLink = links.toString().substring(links.toString().indexOf("<", links.toString().indexOf("<") + 1) + 1, links.toString().indexOf(">", links.toString().indexOf(">") + 1));
					numPages = Integer.parseInt(lastPageLink.substring(lastPageLink.indexOf("?page="), lastPageLink.indexOf("&")).replace("?page=", ""));
				}

				while ((inputLine = in.readLine()) != null) {
					resp.append(inputLine);
				}
				in.close();

				JSONArray jsonArray = new JSONArray(resp.toString());
				JSONObject json = null;
				JSONObject jsonCommit = null;
				Commit commit = null;

				for (int i = 0; i < jsonArray.length(); i++) {
					json = (JSONObject) jsonArray.get(i);
					jsonCommit = json.getJSONObject("commit");

					commit = new Commit();
					commit.setSha(json.getString("sha"));
					commit.setMessage(jsonCommit.getString("message"));
					commit.setUrl(json.getString("url"));
					commit.setRepository(repo);

					commits.add(commit);
				}

				CommitDAO commitDAO = new CommitDAO();
				try {
					if(commits.size() > 5000 || page >= numPages) {
						System.out.println("Batch commits > " + commits.size());
						commitDAO.insertBatch(commits);

						commits = new ArrayList<Commit>();
					}
				} catch(BatchUpdateException e) {
					e.printStackTrace();
				} catch(SQLIntegrityConstraintViolationException e) {
					e.printStackTrace();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			} else {
				System.out.println(responseCode);
			}
			page++;
		}while(page <= numPages);
		return count;
	}
}
