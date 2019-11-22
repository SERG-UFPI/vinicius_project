package br.com.lost.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import br.com.lost.entities.Commit;

public class CommitDAO extends BaseDAO {
	
	private static final String TABLE = " Commits ";
	private static final String INSERT = " INSERT INTO " + TABLE + " (sha, url, message ,idRepository) VALUES (? , ? , ? , ? )";
	private static final String SELECT = " SELECT * FROM " + TABLE + " WHERE idRepository = ?";
	
	public Commit insert(Commit vo) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice = 1;
			st.setString(indice++, vo.getSha());
			st.setString(indice++, vo.getUrl());
			st.setString(indice++, vo.getMessage());
			st.setLong(indice++, vo.getRepository().getId());
			
			super.atualizar();
			
			return vo;
		} finally {
			super.fecharConexao();
		}
	}
	
	public void insertBatch(ArrayList<Commit> commits) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice;
			for (Commit vo : commits) {
				indice = 1;
				
				st.setString(indice++, vo.getSha());
				st.setString(indice++, vo.getUrl());
				st.setString(indice++, vo.getMessage());
				st.setLong(indice++, vo.getRepository().getId());
				
				st.addBatch();
			}
			
			st.executeBatch();
		} finally {
			super.fecharConexao();
		}
	}
	
	public ArrayList<Commit> listCommits(long idRepo) throws SQLException {
		try {
			super.prepararDAO(SELECT);
						
			st.setLong(1,idRepo);
			
			ResultSet rs = super.listar();
			
			Commit commit = null;
			ArrayList<Commit> commits = new ArrayList<>();
			
			while(rs.next()) {
				commit = new Commit();
				commit.setId(rs.getLong("idSeq"));
				commit.setSha(rs.getString("sha"));

				commits.add(commit);
			}
			
			rs.close();
			rs = null;
			
			return commits;
		} finally {
			super.fecharConexao();
		}
	}
}
