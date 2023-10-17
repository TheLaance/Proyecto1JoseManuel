package Servidor;

import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Servidor {

    private static final String URL = "jdbc:mysql://localhost:3306/ABP";
    private static final String USUARIO_DB = "root";
    private static final String CONTRASENA_DB = "Jose1234";
    private static Set<String> IPsConectadas = Collections.synchronizedSet(new HashSet<>());


    public static void main(String[] args) {

        Thread clientAcceptThread = new Thread(() -> handleClientConnections());
        clientAcceptThread.start();

        while (true) {  // Bucle infinito
            verificarUsuariosConectados();

            try {
                // Pausa el hilo durante 1 minuto (60000 milisegundos)
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                System.out.println("Intento de 1 minuto de Sleep");
            }
        }
    }

    private static void handleClientConnections() {
        int port = 56000; // Puerto del servidor

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Servidor en línea. Esperando clientes...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                String IPCliente = clientSocket.getInetAddress().getHostAddress();

                if (IPsConectadas.contains(IPCliente)) {
                    System.out.println("Intento de conexión desde " + IPCliente + " rechazado.");
                    
                    clientSocket.close();
                } else {
                    // Agregar el socket del cliente a la lista de clientes conectados
                    System.out.println("Conexión aceptada desde " + IPCliente);
                    IPsConectadas.add(IPCliente);

                    // Crear un hilo para manejar la comunicación con el cliente
                    Thread clientThread = new Thread(new ClientHandler(clientSocket));
                    clientThread.start();
                }

            }
        } catch (IOException e) {
            System.out.println("Puede ser que tengas un error al intentar abrir el puerto especificado.");
        }
    }
    

    private static void verificarUsuariosConectados() {
        String sql = "SELECT usuario_id FROM usuarios_logeados WHERE timestamp > DATE_SUB(NOW(), INTERVAL 10 MINUTE)";
        try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int usuarioId = rs.getInt("usuario_id");
                System.out.println("El usuario con ID " + usuarioId + " está logeado.");
            }
            String sql2 = "TRUNCATE TABLE usuarios_logeados;";
            PreparedStatement stmt2 = conn.prepareStatement(sql2);
            stmt2.executeUpdate();
             

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static class ClientHandler implements Runnable {

        private Socket clientSocket;
        private DataInputStream in;
        private DataOutputStream out;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            try {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                // Crear flujo de entrada y salida para comunicarse con el cliente
                

                // Leer la opción del cliente (registro o inicio de sesión)
                while (true) {
                    try {

                        String opcion = in.readUTF();
                        switch (opcion) {
                            case "numero": {
                                out.writeUTF("1");
                                break;
                            }
                            case "registro": {
                                // Registrar un nuevo usuario
                                String usuario = in.readUTF();
                                String contrasena = in.readUTF();
                                boolean registroExitoso = registrarUsuario(usuario, contrasena);
                                // Enviar el resultado del registro al cliente
                                out.writeBoolean(registroExitoso);
                                break;
                            }
                            case "login": {
                                // Iniciar sesión con un usuario existente
                                String usuario = in.readUTF();
                                String contrasena = in.readUTF();
                                boolean inicioSesionExitoso = iniciarSesion(usuario, contrasena);
                                String idCliente = buscarIDPorNombre(usuario);
                                // Enviar el resultado del inicio de sesión al cliente                            
                                out.writeUTF(idCliente);
                                registrarLogin(Integer.parseInt(idCliente));
                                out.writeBoolean(inicioSesionExitoso);
                                break;
                            }
                            case "crear_grupo": {

                                String nombreGrupo = in.readUTF();
                                String Id = in.readUTF();
                                String idCliente = buscarIDPorNombre(Id);
                                boolean grupoCreadoExitoso = crearGrupo(nombreGrupo, Id);
                                if (grupoCreadoExitoso == true) {
                                    out.writeBoolean(true);
                                } else {
                                    out.writeBoolean(false);
                                }

                                break;
                            }
                            case "cerrar sesion": {
                                int id = Integer.parseInt(in.readUTF());
                                eliminarUsuario(id);
                            }
                            case "listar": {
                                
                                enviarDatos(in,out);
                                break;
                            }
                            case "listar_ac": {
                                enviarDatosAc(in,out);
                                break;
                            }
                            default:
                                break;

                        }
                    } catch (EOFException e) {
                        // El cliente se desconectó inesperadamente
                        System.out.println("Cliente se desconectó inesperadamente desde: " + clientSocket.getInetAddress());
                        IPsConectadas.remove(clientSocket.getInetAddress());
                        
                        break; // Salir del bucle mientras
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
//        private boolean comprobarGrupo (String id){
//            
//                
//            try {
//                Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB);
//                PreparedStatement checkGroupStmt = conn.prepareStatement("SELECT * FROM grupo WHERE nombre = ?");
//                checkGroupStmt.setString(1, id);
//                ResultSet ejecucion = checkGroupStmt.executeQuery();
//                if (ejecucion.next()) {
//                    // Se encontró una fila en la base de datos, devuelve falso y dice que no se ha creado
//                    return false;
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
//            }
//                
//                
//
//                
//        }
        
        /**
         * 
         * @param in
         * @param out 
         */
        private static void enviarDatos(DataInputStream in, DataOutputStream out) {
            
            String sql = "SELECT usuario FROM usuario";
            try(Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet ejecucion = stmt.executeQuery(); ){
                
                while (ejecucion.next()) {
                String nombre = ejecucion.getString("usuario");
                out.writeUTF(nombre);
                // Espera confirmación del cliente antes de enviar el siguiente nombre
                String ok = in.readUTF();  
                // Aquí esperamos una respuesta del cliente, como "OK", antes de continuar
            }
            // Envía un mensaje de finalización para informar al cliente que no hay más datos
            out.writeUTF("FIN");
            
            } catch (SQLException | IOException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
            }
        }
        
        private static void enviarDatosAc(DataInputStream in, DataOutputStream out) {
            
            String sql = "SELECT usuario FROM usuario WHERE id IN (SELECT usuario_id FROM usuarios_logeados);";
            try(Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet ejecucion = stmt.executeQuery(); ){
                
                while (ejecucion.next()) {
                String nombre = ejecucion.getString("usuario");
                out.writeUTF(nombre);
                // Espera confirmación del cliente antes de enviar el siguiente nombre
                String ok = in.readUTF();  // Aquí esperamos una respuesta del cliente, como "OK", antes de continuar
            }
            // Envía un mensaje de finalización para informar al cliente que no hay más datos
            out.writeUTF("FIN");
            
            } catch (SQLException | IOException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
            }
        }

        private static void eliminarUsuario(int id) {
            String sql = "DELETE FROM usuarios_logeados WHERE usuario_id = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, id); // Establece el valor del parámetro
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Usuario eliminado con éxito.");
                } else {
                    System.out.println("No se encontró un usuario con ese ID.");
                }

            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
            }
        }

        private boolean crearGrupo(String nombregrupo, String id) {
            //Metodo para crear el grupo con un usuario logeado
            String consulta = "SELECT * FROM grupo WHERE nombre = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement checkGroupStmt = conn.prepareStatement(consulta);) {
                checkGroupStmt.setString(1, nombregrupo);
                ResultSet ejecucion = checkGroupStmt.executeQuery();

                if (ejecucion.next()) {
                    // Se encontró una fila en la base de datos, devuelve falso y dice que no se ha creado
                    return false;
                }
                // Si no se ha encontrado lo crea y devuelve verdadero
                PreparedStatement createGroupStmt = conn.prepareStatement("INSERT INTO grupo (nombre, id_dueño) VALUES (?, ?)");
                createGroupStmt.setString(1, nombregrupo);
                createGroupStmt.setString(2, id);
                createGroupStmt.executeUpdate();
                return true;
            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
            }
            return false;

        }

        private String buscarIDPorNombre(String nombreCliente) {
            // Realizar una consulta para buscar la ID por nombre
            String consulta = "SELECT id FROM usuario WHERE usuario = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement consultaSQL = conn.prepareStatement(consulta);) {
                String id;

                consultaSQL.setString(1, nombreCliente);
                ResultSet ejecucion = consultaSQL.executeQuery();

                if (ejecucion.next()) {
                    // Se encontró una fila en la base de datos, devuelve la ID
                    id = String.valueOf(ejecucion.getInt("id"));
                    return id;
                } else {
                    // No se encontró ningún resultado, devuelve -1 o un valor que indique que no se encontró.
                    return "No existe este usuario";
                }
            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
                return "error";
            }
        }

        private boolean registrarUsuario(String usuario, String contrasena) {
            // Comprobar si el usuario ya existe en la base de datos
            String consultaExistencia = "SELECT * FROM usuario WHERE usuario = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement statementExistencia = conn.prepareStatement(consultaExistencia);) {

                statementExistencia.setString(1, usuario);
                ResultSet resultSetExistencia = statementExistencia.executeQuery();

                if (resultSetExistencia.next()) {
                    // El usuario ya existe
                    return false;
                } else {
                    // Insertar el nuevo usuario en la base de datos
                    String consultaInsercion = "INSERT INTO usuario (usuario, contraseña) VALUES (?, ?)";
                    PreparedStatement statementInsercion = conn.prepareStatement(consultaInsercion);
                    statementInsercion.setString(1, usuario);
                    statementInsercion.setString(2, contrasena);
                    statementInsercion.executeUpdate();
                    return true;
                }
            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
                return false;
            }
        }

        private boolean iniciarSesion(String usuario, String contrasena) {
            // Comprobar si el usuario y la contraseña coinciden en la base de datos
            String consulta = "SELECT * FROM usuario WHERE usuario = ? AND contraseña = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement statement = conn.prepareStatement(consulta);) {

                statement.setString(1, usuario);
                statement.setString(2, contrasena);
                ResultSet resultSet = statement.executeQuery();

                return resultSet.next();
                // Si hay una fila, las credenciales son válidas.

            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
                return false;
            }
        }

        private void registrarLogin(int usuarioId) {
            String sql = "INSERT INTO usuarios_logeados (usuario_id) VALUES (?) ON DUPLICATE KEY UPDATE timestamp = CURRENT_TIMESTAMP";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO_DB, CONTRASENA_DB); PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, usuarioId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println("No se ha podido hacer una conexion con la base de datos");
            }
        }

    }
}
