package Presentation;

public class RegisterMenu extends Menu{
    @Override
    public void display() {
        System.out.println("Registro de usuario");
    }

    @Override
    public void handlerInput() {
        System.out.println("Ingrese su nombre de usuario:");
        String username = scanner.nextLine();
        System.out.println("Ingrese su contraseña:");
        String password = scanner.nextLine();
    }
}
