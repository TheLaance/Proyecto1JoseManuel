package Cliente;

import java.io.*;
import java.net.*;
import java.sql.SQLOutput;
import java.util.Properties;
import java.util.Scanner;

public class Cliente {

    private static final String CONFIG_FILE = "client.properties";
    private static boolean loggin = false;
    private static int opcion = 1;
    private static Scanner scanner = new Scanner(System.in);
    private static Scanner scanner2 = new Scanner(System.in);
    private static String usuario_ac;
    private static String Id;

    private static Socket socket;
    private static int max;
    private static String path;

    public static void main(String[] args) {
        Properties properties = new Properties();
        try {
            FileInputStream input = new FileInputStream(CONFIG_FILE);
            properties.load(input);
            input.close();
        } catch (IOException e) {
            System.out.println("No hay archivo de configuracion del cliente o esta mal configurado");
        }

        Id = properties.getProperty("nombre_cliente");
        String serverAddress = properties.getProperty("ip_servidor");
        int serverPort = Integer.parseInt(properties.getProperty("puerto_servidor"));
        max = Integer.parseInt(properties.getProperty("tamano_maximo"));
        path = properties.getProperty("path");

        try {
            socket = new Socket(serverAddress, serverPort);

            DataInputStream in = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());


            out.writeUTF(Id);


            System.out.println("Conectado al servidor: " + serverAddress + ":" + serverPort);
            System.out.println(in.readUTF());
            while (opcion != 0) {
                try {
                    while (opcion != 0 && loggin == false) {

                        System.out.println("");

                        opciones(in, out);

                    }

                    while (loggin == true) {

                        System.out.println("");
                        opciones2(in, out, socket);
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

    public static void opciones2(DataInputStream in, DataOutputStream out, Socket socket) throws IOException {
        System.out.println("");

        System.out.println("Seleccione una opción:");
        System.out.println("1. Crear grupo");
        System.out.println("2. Cerrar sesión");
        System.out.println("3. Listar usuarios totales");
        System.out.println("4. Listar usuarios conectados");
        System.out.println("5. Administrar grupo");
        System.out.println("6. Archivos");

        try {
            opcion = Integer.parseInt(scanner2.nextLine());
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
                Extractor(in, out);
            } else if (opcion == 5) {
                System.out.println("1. Borrar grupo");
                System.out.println("2. Añadir usuario a grupo");
                System.out.println("3. Listar usuarios del grupo");
                System.out.println("4. Borrar usuario de grupo");
                Integer opcion3 = Integer.parseInt(scanner.nextLine());
                if (opcion3 == 1) {
                    out.writeUTF("conocerGrupos");
                    out.writeUTF(usuario_ac);
                    System.out.println("Grupos que puedes borrar");
                    Extractor(in, out);
                    System.out.println("Que nombre de grupo quieres borrar?");
                    String grupo = scanner.nextLine();
                    if (!grupo.isEmpty()) {
                        out.writeUTF(grupo);
                    }
                    if (in.readBoolean() == true) {
                        System.out.println("Grupo " + grupo + " ha quedado eliminado.");
                    } else {
                        System.out.println("No se ha eliminado el grupo, prueba a volver a intentarlo");
                    }

                } else if (opcion3 == 2) {
                    out.writeUTF("addUser");
                    out.writeUTF(usuario_ac);
                    Extractor(in, out);
                    System.out.println("Selecciona el grupo: ");
                    out.writeUTF(scanner.nextLine());
                    System.out.println("");
                    while (true) {
                        String nombre = in.readUTF();

                        System.out.println("");// Leer un valor enviado por el servidor

                        if ("FIN".equals(nombre)) {
                            // Si el dato recibido es "FIN", significa que ya no hay más datos para recibir
                            System.out.println("Todos los datos han sido recibidos.");
                            break;
                        }
                        System.out.println(nombre);
                        // Enviar confirmación al servidor para recibir el siguiente dato
                        out.writeUTF("OK");
                    }
                    System.out.println("Que usuario quieres añadir: ");
                    out.writeUTF(scanner.nextLine());
                    if (in.readBoolean() == true) {
                        System.out.println("Usuario añadido");
                    } else {
                        System.out.println("Prueba de nuevo");
                    }

                } else if (opcion3 == 3) {
                    out.writeUTF("saberUser");
                    out.writeUTF(usuario_ac);
                    Extractor(in, out);

                    System.out.println("Que grupo quieres selecionar para saber los usuarios: ");
                    out.writeUTF(scanner.nextLine());
                    System.out.println("");

                    Extractor(in, out);

                } else  {

                }
            } else if (opcion == 6) {
                System.out.println();
                OutputStream outfilename = socket.getOutputStream();
                OutputStream fileOutputStream = socket.getOutputStream();
                System.out.println("1. Enviar Archvio");
                Integer opcion3 = Integer.parseInt(scanner.nextLine());


                if (opcion3 == 1){
                    String filePath;
                    out.writeUTF("enviar archivo");
                    System.out.println("Indica la ruta del archivo con la extension");
                    filePath = scanner2.nextLine();
                    File file = new File(filePath);
                    String FileName = file.getName();
                    byte[] FileNameByte = FileName.getBytes();
                    if (isFileSizeValid(file)){
                        System.out.println("Indica que permiso deseas, Privado = -1, publico = 0, grupo = 1");
                        int p = scanner.nextInt();
                        out.writeInt(p);
                        outfilename.write(FileNameByte);
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] buffer = new byte[1024];
                        int bytesRead;

                        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                            fileOutputStream.write(buffer, 0, bytesRead);
                        }

                        System.out.println("Archivo enviado con éxito: " + FileName);
                        fileInputStream.close();
                    }else {
                        System.out.println("El archivo es demasiado pesado");
                    }


                }
                outfilename.close();
                fileOutputStream.close();

            } else {
                System.out.println("Opción no válida.");
            }
        } catch (EOFException e) {
            System.out.println("El cliente se ha desconectado inesperadamente");
        } catch (NumberFormatException e) {
            System.out.println("Usa un caracter correcto");
        }
    }

    private static boolean isFileSizeValid (File file){

        return file.length() <= max;
    }



    private static void Extractor(DataInputStream in, DataOutputStream out) throws IOException {
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
    }


    public static void opciones(DataInputStream in, DataOutputStream out) throws IOException {
        System.out.println("Seleccione una opción:");
        System.out.println("1. Registro");
        System.out.println("2. Inicio de sesión");
        System.out.println("3. Listar usuarios totales");
        System.out.println("4. Listar usuarios conectados");
        System.out.println("0. Finalizar");
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Usa un caracter correcto");
        }


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
                Extractor(in, out);
                break;
            }
            case 0: {
                out.writeUTF("cerrar conexion");
                out.writeUTF(Id);
                socket.close();
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