package data;

import data.entity.User;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class UserDB {
    /**
     * Insertar customers directamente en la base de datos.
     *
     * @param con
     * @param u
     * @throws DadesException
     */

    public static void insertUserBD(Connection con, User u) throws DadesException {
        Statement sentencia;
        int id = 0;
        try {
            sentencia = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            sentencia.executeQuery("SELECT * FROM usuario");
            ResultSet rs = sentencia.getResultSet();


            if (rs.next()) {
                rs.last();
                id = rs.getInt("id") + 1;
            }


            rs.moveToInsertRow();
            rs.updateInt("id", id);
            rs.updateString("usuario", u.getName());


            rs.insertRow();

        } catch (SQLException ex) {
            throw new DadesException("Error en " + ex.getStackTrace()[0].getMethodName() + ":" + ex.toString());
        }

    }


    /**
     * Obtener todos los customers de la base de datos
     *
     * @param con
     * @return
     * @throws DadesException
     */

    public static ArrayList<User> getUserBD(Connection con) throws DadesException {
        ArrayList<User> ret = new ArrayList<>();

        Statement sentencia;

        try {
            sentencia = con.createStatement();
            sentencia.executeQuery("SELECT * FROM usuario");
            ResultSet rs = sentencia.getResultSet();
            while (rs.next()) {
                ret.add(new User(rs.getInt("id"), rs.getString("usuario")));
            }
        } catch (SQLException ex) {
            throw new DadesException("Error en " + ex.getStackTrace()[0].getMethodName() + ":" + ex.toString());
        }

        return ret;
    }

}
