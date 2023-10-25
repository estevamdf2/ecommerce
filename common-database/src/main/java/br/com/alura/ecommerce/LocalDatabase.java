package br.com.alura.ecommerce;

import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:target/" + name + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    //Yes, this is way is too generic.
    // according to your database tool, avoid injection.
    public void createIfNotExists(String sql){
        try {
            connection.createStatement().execute(sql);
        } catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    public void update(String statement, String ... params) throws SQLException {
        var preparedStatement = getPreparedStatement(statement, params);
        preparedStatement.execute();
//        System.out.println("Usuário " + uuid + " e " + email + " adicionado");
    }

    public ResultSet query(String sql, String... params) throws SQLException {
        var preparedStatement = getPreparedStatement(sql, params);
        return preparedStatement.executeQuery();
    }

    private PreparedStatement getPreparedStatement(String sql, String[] params) throws SQLException {
        var preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i+1, params[i]);
        }
        return preparedStatement;
    }

    public void close() throws SQLException {
        connection.close();
    }
}