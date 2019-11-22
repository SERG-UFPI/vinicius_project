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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.lost.dao.CommitDAO;
import br.com.lost.dao.FileDAO;
import br.com.lost.dao.RepositoryDAO;
import br.com.lost.entities.Commit;
import br.com.lost.entities.File;
import br.com.lost.entities.Repository;
import br.com.lost.util.Constantes;

public class CommitFilesFachada {
	
	private static int responseCode;
	private static int tokenIndex = 0;
	private static String TOKEN;
	
	public static void main(String[] args) {
		ArrayList<Repository> repositories;
		ArrayList<Commit> commits;
		TOKEN = Constantes.tokens[tokenIndex];
		RepositoryDAO repoDAO = new RepositoryDAO();
		CommitDAO commitDAO = new CommitDAO();
		try {
			repositories = new ArrayList<Repository>();
			Repository repository = new Repository();
			repository.setLanguage("java");
			
			repositories = repoDAO.listarRepositorios(repository);

			for (Repository item : repositories) {
				commits = new ArrayList<Commit>();
				
				commits = commitDAO.listCommits(item.getId());
				gravarArquivos(item, commits);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public static void gravarArquivos(Repository repo, ArrayList<Commit> commits) throws IOException, JSONException {
		ArrayList<File> ultimosArquivos = new ArrayList<File>();
		System.out.println("# " + repo.getName());
		for (Commit commit : commits) {
			ultimosArquivos.addAll(commitFiles(repo, commit.getSha(), commit.getId()));
			
			try {
				FileDAO fileDAO = new FileDAO();
				if(ultimosArquivos.size() >= 100) {
					System.out.println("Batch files > " + ultimosArquivos.size());
					fileDAO.insertBatch(ultimosArquivos);
					
					ultimosArquivos = new ArrayList<File>();
				}
				
			} catch(BatchUpdateException e) {
				e.printStackTrace();
			} catch(SQLIntegrityConstraintViolationException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		try {
			FileDAO fileDAO = new FileDAO();
			if(ultimosArquivos.size() > 0) {
				System.out.println("Batch files > " + ultimosArquivos.size());
				System.out.println("FIM");
				fileDAO.insertBatch(ultimosArquivos);
			}
		} catch(BatchUpdateException e) {
			e.printStackTrace();
		} catch(SQLIntegrityConstraintViolationException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<File> commitFiles(Repository repo, String sha, long idSeq) throws IOException, JSONException{
		ArrayList<File> files = new ArrayList<File>();

		String url = Constantes.LIST_FILES_COMMIT.replace("<owner>", repo.getOwner()).replace("<repo>", repo.getName()).replace("<sha>", sha);

		URL obj = new URL(url);
		HttpURLConnection con;
		do {
			con = (HttpURLConnection) obj.openConnection();
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

		if(responseCode == HttpURLConnection.HTTP_OK){
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer resp = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				resp.append(inputLine);
			}
			in.close();

			JSONObject jsonCommit = new JSONObject(resp.toString());
			JSONArray jsonFiles = jsonCommit.getJSONArray("files");
			JSONObject json = null;
			File file = null;
			for (int i = 0; i < jsonFiles.length(); i++) {
				file = new File();
				json = (JSONObject) jsonFiles.get(i);

				file.setSha(json.getString("sha"));
				file.setFilename(json.getString("filename"));
				file.setAdditions(json.getInt("additions"));
				file.setDeletions(json.getInt("deletions"));
				file.setChanges(json.getInt("changes"));
				file.setStatus(json.getString("status"));
				
				file.setCommit(new Commit(sha, idSeq));
				file.setRepository(repo);
				
				files.add(file);
			}
		}
		return files;
	}
	
}
