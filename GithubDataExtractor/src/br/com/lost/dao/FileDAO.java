package br.com.lost.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import br.com.lost.entities.File;

public class FileDAO extends BaseDAO {
	
	private static final String TABLE = " Files ";
	private static final String INSERT_SELECT = " INSERT INTO " + TABLE + " (sha, filename, status, additions , deletions, changes ,idCommit, shaCommit, idRepository) VALUES (? , ? , ? , ? , ? , ? , ? , ?, ?)";
	
	public File insert(File vo) throws SQLException {
		try {
			super.prepararDAO(INSERT_SELECT);
			
			int indice = 1;
			st.setString(indice++, vo.getSha());
			st.setString(indice++, vo.getFilename());
			st.setString(indice++, vo.getStatus());
			st.setInt(indice++, vo.getAdditions());
			st.setInt(indice++, vo.getDeletions());
			st.setInt(indice++, vo.getChanges());
			st.setLong(indice++, vo.getCommit().getId());
			st.setString(indice++, vo.getCommit().getSha());
			st.setLong(indice++, vo.getRepository().getId());

			super.atualizar();
			
			return vo;
		} finally {
			super.fecharConexao();
		}
	}
	
	public void insertBatch(ArrayList<File> files) throws SQLException {
		try {
			super.prepararDAO(INSERT_SELECT);
			
			int indice;
			for (File vo : files) {
				indice = 1;
				
				st.setString(indice++, vo.getSha());
				st.setString(indice++, vo.getFilename());
				st.setString(indice++, vo.getStatus());
				st.setInt(indice++, vo.getAdditions());
				st.setInt(indice++, vo.getDeletions());
				st.setInt(indice++, vo.getChanges());
				st.setLong(indice++, vo.getCommit().getId());
				st.setString(indice++, vo.getCommit().getSha());
				st.setLong(indice++, vo.getRepository().getId());
				
				st.addBatch();
			}
			
			st.executeBatch();
		} finally {
			super.fecharConexao();
		}
	}
}
