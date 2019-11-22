package br.com.lost.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import br.com.lost.entities.IssueEvent;

public class IssueEventDAO extends BaseDAO {
	
	private static final String TABLE = " IssueEvents ";
	private static final String INSERT = " INSERT INTO " + TABLE + " (id, commitId, url, idIssue ,idRepository ) VALUES ( ? , ? , ? , ? , ? )";
	
	public IssueEvent insert(IssueEvent vo) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice = 1;
			st.setLong(indice++, vo.getId());
			st.setString(indice++, vo.getCommitId());
			st.setString(indice++, vo.getUrl());
			st.setLong(indice++, vo.getIssue().getId());
			st.setLong(indice++, vo.getRepository().getId());
			
			super.atualizar();
			
			return vo;
		} finally {
			super.fecharConexao();
		}
	}
	
	public void insertBatch(ArrayList<IssueEvent> issueEvents) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice;
			for (IssueEvent vo : issueEvents) {
				indice = 1;
				
				st.setLong(indice++, vo.getId());
				st.setString(indice++, vo.getCommitId());
				st.setString(indice++, vo.getUrl());
				st.setLong(indice++, vo.getIssue().getId());
				st.setLong(indice++, vo.getRepository().getId());
				
				st.addBatch();
			}
			
			st.executeBatch();
		} finally {
			super.fecharConexao();
		}
	}
}
