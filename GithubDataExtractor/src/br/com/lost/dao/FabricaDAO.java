package br.com.lost.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import br.com.lost.util.Constantes;

public class FabricaDAO {

	private static String urlMYSQL;

	private static String nomeUsuarioMySQL;

	private static String senhaUsuarioMySQL;

	private static boolean isDeamon = false;

	public static boolean isDeamon() {
		return isDeamon;
	}

	public static void setDeamon(boolean isDeamon) {
		FabricaDAO.isDeamon = isDeamon;
	}

	public static Connection criarConexao() throws SQLException {
		setarParametrosConexao(Constantes.DATABASE_URL,Constantes.DATABASE_USER,Constantes.DATABASE_PASSWORD);
		return criarConexaoSimples();
	}

	public static Connection criarConexaoSimples() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			return DriverManager.getConnection(urlMYSQL, nomeUsuarioMySQL, senhaUsuarioMySQL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void setarParametrosConexao(String pUrlMysql, String pNomeUsuario, String pSenhaUsuario) {
		urlMYSQL = pUrlMysql;
		nomeUsuarioMySQL = pNomeUsuario;
		senhaUsuarioMySQL = pSenhaUsuario;
		isDeamon = true;
	}

}