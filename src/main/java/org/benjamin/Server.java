package org.benjamin;


// our server class will be responsible for listening to clients who wish to connect. and when they do it'll create
// a new thread to handle them.
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket; // this object will be in charge of listening to incoming connections or cleints and
    //creating a socket object to communicate with them
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }


    public void startServer() {//this method will be in charge to make sure our server stays running.
        //we want our server to be running until our server socket is closed:
        try{
            while (!serverSocket.isClosed()) {
                Socket socket = serverSocket.accept(); //while it isnt closed, we are gonna wait for our client to connect.
                //.accept() is a blocking method meaning our program will be halted here until our client connects. so when a
                //client connects, serversocket object is returned which is used to communicate with the client.
                System.out.println("a new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket ); //each object of this class will be responsible for
                //communicating with the client.
                Thread thread = new Thread(clientHandler);
                thread.start();
                //runnable is for a class whose instances will be run in its own separate thread.


            }
        } catch (Exception e){
            e.printStackTrace();
        }

    }
    public void closeServerSocket(){
        try{
            if(serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1111); // this is saying server will listen from this port number.
            Server server = new Server(serverSocket);
            server.startServer();


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
