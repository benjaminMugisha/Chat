package org.benjamin;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    //this list is for every clientHandler object we have instantiated.
    public static List<ClientHandler> clientHandlers = new ArrayList<>();/*the main purpose of this list is to keep track
    clients so whenever a client sends a message we can loop through an arraylist of clients and send the message to every client.*/
    private Socket socket;// this socket will be passed from a server class. this will be used to estabilish a connection btn a client
    // and server.
    BufferedReader bufferedReader; //to read the data from the clients.
    BufferedWriter bufferedWriter; //to send to other clients.
    private String userName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));//in java, there's 2 types of streams=>
            // byte streams(ends with stream) and character streams(ends with writer). we are sending character streams so wew wrap the byte streams.
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.userName = bufferedReader.readLine();
            clientHandlers.add(this);//add the client to the arraylist so they can receive messages from other users.
            // this signifies clientHandler object as defined in the List interface.
            broadcastMessage("SERVER: " + userName + " has entered the chat!");

        } catch (IOException e) {
            closeEverything(socket, bufferedWriter, bufferedReader);
        }

    }


    @Override
    public void run() { // everything in this method is what is run on a different thread.
        //we need a separate thread to listen to messages because this is a blocking method and we don't want all the threads to stop
        //and wait for the messages from the client. and another thread to work with the rest of the applications. very important concept
        String messageFromClient; // to listen to the message from the client
        while (socket.isConnected()) {//to listen as long as we are connected to the client
            try {
                messageFromClient = bufferedReader.readLine(); // this is the blocking operation.
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
                if (!clientHandler.userName.equals(userName)) {//broadcast the message to everyone but the user who sent it.thats why we have username variable.
                    clientHandler.bufferedWriter.write(message);
                    clientHandler.bufferedWriter.newLine();//client is waiting on a new line because they are using readLine() and we have to explicitly do that.
                    clientHandler.bufferedWriter.flush();// we have to manually flush the buffer because the message send the buffer maybe wont fill it
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedWriter, bufferedReader);
            }
        }
    }



    //to tell users a user has left the chat
    public void removeClientHandler(){
        clientHandlers.remove(this);//firstly, remove them from the list. and we dont send them messages anymore because connection was closed.
        broadcastMessage("SERVER " + userName + " has left the chat");

    }
    public void closeEverything(Socket socket, BufferedWriter bufferedWriter, BufferedReader bufferedReader) {
        removeClientHandler(); // becasuse the users are leaving the chat
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
