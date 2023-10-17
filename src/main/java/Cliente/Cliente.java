package Cliente;

import java.io.*;
import java.net.*;
import java.util.Properties;
import java.util.Scanner;

public class Cliente {

    private static final String CONFIG_FILE = "client.properties";
    private static boolean loggin = false;
    private static int opcion = 1;
    private static Scanner scanner = new Scanner(System.in);
    private static String usuario_ac;

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(CONFIG_FILE);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            System.out.println("No hay archivo de configuracion del cliente o esta mal configurado");
        }

        String serverAddress = properties.getProperty("ip_servidor");
        int serverPort = Integer.parseInt(properties.getProperty("puerto_servidor"));

        try {
            Socket socket = new Socket(serverAddress, serverPort);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            System.out.println("Conectado al servidor: " + serverAddress + ":" + serverPort);
            while (opcion != 0) {
                try {
                    while (opcion != 0 && loggin == false) {
                        System.out.println("");
                        System.out.println("");

                        opciones(in, out);

                    }

                    while (loggin == true) {
                        System.out.println("");
                        System.out.println("");
                        opciones2(in, out);
                    }
                } catch (EOFException | SocketException e) {
                    System.out.println("Tienes otro cliente o el servidor no esta conectado");
                    break;  // Sal del bucle y cierra el socket
                }
            }

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void opciones2(DataInputStream in, DataOutputStream out) throws IOException {
        System.out.println("");

        System.out.println("Seleccione una opción:");
        System.out.println("1. Crear grupo");
        System.out.println("2. Cerrar sesión");
        System.out.println("3. Listar usuarios totales");
        System.out.println("4. Listar usuarios conectados");
        System.out.println("5. Administrar grupo");

        try {
            opcion = Integer.parseInt(scanner.nextLine());
            if (opcion == 1) {
                System.out.println("Tu id es: " + usuario_ac);
                System.out.println("Nombre del grupo que quieres crear: ");
                String nameGrupo = scanner.nextLine();
                if (!nameGrupo.isBlank()) {
                    out.writeUTF("crear_grupo");
                    out.writeUTF(nameGrupo);
                    out.writeUTF(usuario_ac);
                    boolean grupoCreado = in.readBoolean();
                    if (grupoCreado == true) {
                        System.out.println("El grupo " + nameGrupo + " ha sido creado.");
                    } else {
                        System.out.println("El grupo " + nameGrupo + " ya tiene dueño crealo de nuevo.");
                    }
                } else {
                    System.out.println("El nombre del grupo no puede estar vacio.");
                }
            } else if (opcion == 2) {
                out.writeUTF("cerrar sesion");
                out.writeUTF(usuario_ac);
                usuario_ac = null;
                loggin = false;
            } else if (opcion == 3) {
                out.writeUTF("listar");
                System.out.println("Esta es la lista de usuarios totales registrados");
                while (true) {
                    String nombre = in.readUTF();
                    System.out.println(nombre);
                    System.out.println("");// Leer un valor enviado por el servidor

                    if ("FIN".equals(nombre)) {
                        // Si el dato recibido es "FIN", significa que ya no hay más datos para recibir
                        System.out.println("Todos los datos han sido recibidos.");
                        break;
                    }
                    // Enviar confirmación al servidor para recibir el siguiente dato
                    out.writeUTF("OK");
                }

            } else if (opcion == 4) {
                out.writeUTF("listar_ac");
                System.out.println("Esta es la lista de usuarios logeados ahora mismo");
                while (true) {
                    String nombre = in.readUTF();

                    if ("FIN".equals(nombre)) {
                        // Si el dato recibido es "FIN", significa que ya no hay más datos para recibir
                        System.out.println("Todos los datos han sido recibidos.");
                        break;
                    }
                    System.out.println(nombre);
                    System.out.println("");// Leer un valor enviado por el servidor
                    // Enviar confirmación al servidor para recibir el siguiente dato
                    out.writeUTF("OK");
                }
            } else if (opcion == 5) {
                System.out.println("1. Borrar grupo");
                System.out.println("2. Listar usuarios del grupo");
                System.out.println("3. Borrar usuario de grupo");
                System.out.println("4. Añadir usuario a grupo");
                String opcion3 = scanner.nextLine();
                if(opcion3 == "1"){
                    System.out.println("Grupos que puedes borrar");
                    out.writeUTF("");
                    
                }else if (opcion3 == "2"){
                    
                }else{
                    
                }
            } else {
                System.out.println("Opción no válida.");
            }
        } catch (EOFException e) {
            System.out.println("El cliente se ha desconectado inesperadamente");
        }
    }



    public static void opciones(DataInputStream in, DataOutputStream out) throws IOException {
        System.out.println("Seleccione una opción:");
        System.out.println("1. Registro");
        System.out.println("2. Inicio de sesión");
        System.out.println("3. Listar usuarios totales");
        System.out.println("4. Listar usuarios conectados");
        System.out.println("0. Finalizar");
        opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {
            case 1: {

                out.writeUTF("registro");
                System.out.print("Introduce tu nombre de usuario: ");
                String usuario = scanner.nextLine();

                System.out.print("Introduce tu contraseña: ");
                String contrasena = scanner.nextLine();

                if (!usuario.isBlank() && !contrasena.isBlank()) {
                    out.writeUTF(usuario);
                    out.writeUTF(contrasena);

                    boolean registroExitoso = in.readBoolean();
                    if (registroExitoso) {
                        System.out.println("Registro exitoso.");
                    } else {
                        System.out.println("El nombre de usuario ya está en uso.");
                    }
                    break;
                } else {
                    System.out.println("Necesita una contraseña y usuario");
                    break;
                }

            }
            case 2: {

                out.writeUTF("login");
                System.out.print("Introduce tu nombre de usuario: ");
                String usuario = scanner.nextLine();
                out.writeUTF(usuario);

                System.out.print("Introduce tu contraseña: ");
                String contrasena = scanner.nextLine();
                out.writeUTF(contrasena);

                usuario_ac = in.readUTF();
                boolean inicioSesionExitoso = in.readBoolean();
                if (inicioSesionExitoso) {
                    System.out.println("Inicio de sesión exitoso.");
                    loggin = true;
                } else {
                    System.out.println("Nombre de usuario o contraseña incorrectos.");
                }
                break;
            }

            case 3: {
                out.writeUTF("listar");
                System.out.println("Esta es la lista de usuarios totales registrados");
                while (true) {
                    String nombre = in.readUTF();
                    System.out.println(nombre);
                    System.out.println("");// Leer un valor enviado por el servidor

                    if ("FIN".equals(nombre)) {
                        // Si el dato recibido es "FIN", significa que ya no hay más datos para recibir
                        System.out.println("Todos los datos han sido recibidos.");
                        break;
                    }
                    // Enviar confirmación al servidor para recibir el siguiente dato
                    out.writeUTF("OK");
                }
                break;

            }
            case 4: {
                out.writeUTF("listar_ac");
                System.out.println("Esta es la lista de usuarios logeados ahora mismo");
                while (true) {
                    String nombre = in.readUTF();

                    if ("FIN".equals(nombre)) {
                        // Si el dato recibido es "FIN", significa que ya no hay más datos para recibir
                        System.out.println("Todos los datos han sido recibidos.");
                        break;
                    }
                    System.out.println(nombre);
                    System.out.println("");// Leer un valor enviado por el servidor
                    // Enviar confirmación al servidor para recibir el siguiente dato
                    out.writeUTF("OK");
                }
                break;
            }
            case 0: {

                System.out.println("Sesion Finalizada con exito.");
                break;
            }
            default: {

                System.out.println("Opción no válida.");
                break;
            }

        }

    }

}