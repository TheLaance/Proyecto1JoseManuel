package logic;

import data.DadesException;
import data.UserDB;

public class UserLogic {


    public void insertUserLogic(String username, String password) throws DadesException {
        if(UserDB.userExist(null, username)){
            System.out.println("El usuario ya existe");
        }
        System.out.println("Usuario registrado");
    }

    public void listConnectedUsers() {
        System.out.println("Lista de usuarios conectados");
    }
}
