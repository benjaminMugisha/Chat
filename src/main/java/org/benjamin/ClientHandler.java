package org.benjamin;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;//to estabilish a connection btn a client and server.
    BufferedReader bufferedReader;
    BufferedWriter bufferedWriter;
    private String userName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));/.
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = bufferedReader.readLine();
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + userName + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }


    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {//to listen as long as we are connected to the client
            try {
                messageFromClient = bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
                break; // so when the client is disconnected, get out.
            }
        }


    }

    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.userName.equals(userName)) {//broadcast the message to everyone but the user who sent it.
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();//client is waiting on a new line because they are using readLine().
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }
    //to tell users a user has left the chat
    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER " + userName + " has left the chat");

    }
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler();
        try{
            if(bufferedWriter != null)
                bufferedWriter.close();
            if(bufferedReader != null)
                bufferedReader.close();
            if(socket != null)
                socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
