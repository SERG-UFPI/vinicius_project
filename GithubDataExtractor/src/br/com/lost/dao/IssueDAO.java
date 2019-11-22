package br.com.lost.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import br.com.lost.entities.Issue;

public class IssueDAO extends BaseDAO {
	
	private static final String TABLE = " Issues ";
	private static final String INSERT = " INSERT INTO " + TABLE + " (id, state, title, body, url ,idRepository ) VALUES ( ? , ? , ? , ? , ? , ? )";
	
	public Issue insert(Issue vo) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice = 1;
			st.setLong(indice++, vo.getId());
			st.setString(indice++, vo.getState());
			st.setString(indice++, vo.getTitle());
			st.setString(indice++, vo.getBody());
			st.setString(indice++, vo.getUrl());
			st.setLong(indice++, vo.getRepository().getId());
			
			super.atualizar();
			
			return vo;
		} finally {
			super.fecharConexao();
		}
	}
	
	public void insertBatch(ArrayList<Issue> issues) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice;
			for (Issue vo : issues) {
				indice = 1;
				
				st.setLong(indice++, vo.getId());
				st.setString(indice++, vo.getState());
				st.setString(indice++, vo.getTitle());
				st.setString(indice++, vo.getBody());
				st.setString(indice++, vo.getUrl());
				st.setLong(indice++, vo.getRepository().getId());
				
				st.addBatch();
			}
			
			st.executeBatch();
		} finally {
			super.fecharConexao();
		}
	}
}
