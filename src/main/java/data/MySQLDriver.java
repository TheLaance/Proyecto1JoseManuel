package data;

import data.entity.CredentialDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDriver {

    public static Connection conectarBD(CredentialDB cr) throws SQLException {
        String bd = cr.getDb();
        String usuario = cr.getUsername();
        String password = cr.getPassword();

        Connection ret;

        ret = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + bd + "?useUnicode=true&"
                + "useJDBCCompliantTimezoneShift=true&"
                + "useLegacyDatetimeCode=false&serverTimezone=UTC", usuario, password);


        return ret;

    }
}
