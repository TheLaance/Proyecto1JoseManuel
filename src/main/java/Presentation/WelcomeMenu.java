package Presentation;

import java.util.Scanner;

public class WelcomeMenu extends Menu

{
    public WelcomeMenu() {
        super();
    }

    @Override
    public void display() {
        System.out.println("Bienvenido al sistema de mensajería");
        System.out.println("1. Registro");
        System.out.println("2. Inicio de sesión");
        System.out.println("3. Listar usuarios totales");
        System.out.println("4. Listar usuarios conectados");
        System.out.println("0. Finalizar");
    }

    @Override
    public void handlerInput() {
        System.out.println("Seleccione una opción:");
        String input = scanner.nextLine();

        switch (input){
            case "1":
                RegisterMenu registerMenu = new RegisterMenu();
                registerMenu.run();
                break;
            case "2":
                LoginMenu loginMenu = new LoginMenu();
                loginMenu.run();
                break;
            case "3":
                ListUsersMenu listUsersMenu = new ListUsersMenu();
                listUsersMenu.run();
                break;
            case "4":
                ListConnectedUsersMenu listConnectedUsersMenu = new ListConnectedUsersMenu();
                listConnectedUsersMenu.run();
                break;
            case "0":
                System.out.println("Hasta pronto!");
                System.exit(0);
                break;
            default:
                System.out.println("Opción no válida");
                break;
        }
    }
}
