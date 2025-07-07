package edu.coursera.distributed;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
    throws IOException {
        while (true) {
            // aceptar una nueva conexión de cliente
            Socket clienteSocket = socket.accept();
            // leemos la solicitud HTTP
            InputStream input = clienteSocket.getInputStream();
            OutputStream output = clienteSocket.getOutputStream();
            // leemos la primera linea del request HTTP
            StringBuilder requestBuilder = new StringBuilder();
            int c;
            while ((c = input.read()) != -1) {
                requestBuilder.append((char) c);
                if (requestBuilder.toString().endsWith("\r\n\r\n")) {
                    break;
                }
            }
            String request = requestBuilder.toString();
            String[] lines = request.split("\r\n");
            if (lines.length == 0 || !lines[0].startsWith("GET ")) {
                clienteSocket.close();
                continue;
            }
            // obtener la ruta del archivo solicitado
            String firstLine = lines[0]; // GET /path/file.txt HTTP/1.1
            String[] tokens = firstLine.split(" ");
            if (tokens.length < 2) {
                clienteSocket.close();
                continue;
            }
            String path = tokens[1]; // "/path/file.txt"
            PCDPPath pcdpPath = new PCDPPath(path);

            // leer archivo del sistema y preparar la respuesta HTTP
            String contenido = fs.readFile(pcdpPath);
            if (contenido != null) {
                // respuesta 200 OK
                String respuesta = "HTTP/1.0 200 OK\r\n" + "Server: FileServer\r\n" + "\r\n" + contenido;
                output.write(respuesta.getBytes());
            } else {
                // respuesta 404 Not Found
                String respuesta = "HTTP/1.0 404 Not Found\r\n" + "Server: FileServer\r\n" + "\r\n";
                output.write(respuesta.getBytes());
            }
            // cerrar los recursos
            output.close();
            input.close();
            clienteSocket.close();
        }
    }

     
}
