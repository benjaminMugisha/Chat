package org.benjamin;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void sendMessage() {
        try{
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter. flush();

            Scanner scanner = new Scanner(System.in); // to get input from the console
            while(socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter. flush();

            }

        } catch (IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void ListenMessage() { /*remember listening for messages is a blocking operation so we need to use threads.
       we are gonna do this in a different way than implementing a runnable interface. instead we're going to create a
       new thread and pass in a runnable object.
       so this class is gonna be waiting for messages sent from the broadcast(). and each client will have a separate thread
       waiting for the message and when its sent we'll loop through each connection(for loop in broadcast()) send down the message,
       and  when the clients get it it'll be printed in the console.
       .*/

        new Thread(new Runnable() {
            @Override
            public void run() { // whatever will be inside of this run method will be what is executed on a separate thread
                String messageFromGroupChat;
                try {
                    while (socket.isConnected()) {
                        messageFromGroupChat = bufferedReader.readLine();
                        System.out.println(messageFromGroupChat);
                    }
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }

            }
        }).start(); //we are not referencing it we're creating an object and starting it straight away.
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
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

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter your username:  ");
        String userName = scanner.nextLine();

        //now we create the socket object thats gonna be passed to the client. this is where we're gonna connect to the port
        //where the server is listening on. (check main class of Server)

            Socket socket1 = new Socket("localhost", 1111); // first is the ip address you want to connect to but we are using our machine localhost.
            Client client = new Client(socket1, userName); //instatiating this class's constructor.
            client.ListenMessage();
            client.sendMessage();

    }
}
//to run multiple instances to test this program, run client clss and server class, then go to right top corner, click edit configurations
// click modify options then allow multiple instances.
