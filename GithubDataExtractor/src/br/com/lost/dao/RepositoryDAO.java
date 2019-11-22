package br.com.lost.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import br.com.lost.entities.Repository;

public class RepositoryDAO extends BaseDAO {
	
	private static final String TABLE = " Repositories ";
	private static final String INSERT = " INSERT INTO " + TABLE + " (id, name, owner ,language ,url ) VALUES (? , ? , ? , ? , ? )";
	private static final String SELECT = " SELECT * FROM " + TABLE + " WHERE language = ? ";
	
	public Repository insert(Repository vo) throws SQLException {
		try {
			super.prepararDAO(INSERT);
			
			int indice = 1;
			st.setLong(indice++, vo.getId());
			st.setString(indice++, vo.getName());
			st.setString(indice++, vo.getOwner());
			st.setString(indice++, vo.getLanguage());
			st.setString(indice++, vo.getUrl());
			
			super.atualizar();
			
			return vo;
		} finally {
			super.fecharConexao();
		}
	}
	
	public ArrayList<Repository> listarRepositorios(Repository repo) throws SQLException {
		try {
			super.prepararDAO(SELECT);
			
			if(repo.getLanguage() != null && !repo.getLanguage().isEmpty()) {
				st.setString(1,repo.getLanguage());
			}

			ResultSet rs = super.listar();
			
			Repository repositorio = null;
			ArrayList<Repository> repositorios = new ArrayList<>();
			
			while(rs.next()) {
				repositorio = new Repository();
				repositorio.setId(rs.getInt("id"));
				repositorio.setName(rs.getString("name"));
				repositorio.setOwner(rs.getString("owner"));

				repositorios.add(repositorio);
			}
			
			rs.close();
			rs = null;
			
			return repositorios;
		} finally {
			super.fecharConexao();
		}
	}
}
