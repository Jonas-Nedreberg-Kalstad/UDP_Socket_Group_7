package no.ntnu.gruppe7;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

// To complete this task we have used code examples from:
// https://github.com/ntnu-datakomm/server-side/blob/main/example-udp-server/src/main/java/no/ntnu/UdpClient.java

/**
 * Send a task request to UDP server, then get a response (task) from server and solve it.
 * Then send the answer to server to get confirmation on whether the answer is correct or not.
 */
public class UdpSocketHandler {

    // socket used for establishing connection to UDP server
    DatagramSocket socket;

    /**
     * Constructor to create one single DatagramSocket to be used in this class
     *
     * @throws SocketException Exception for creating or accessing socket
     */
    UdpSocketHandler() throws SocketException {
        this.socket = new DatagramSocket();
    }

    /**
     * Runs the listen, response etc. According to the UDP protocol
     */
    public void run() {
        // Sending 5 task requests to server and solving them
        for (int i = 0; i<5; i++){
            if (sendToServer("task")){
                String task = listenForResponse();
                String answer = solveTaskResponseFromServer(task);
                if (answer != null){
                    // Sends answer to server
                    sendToServer(answer);
                    String response = listenForResponse();
                    // Checks if response is successful
                    // Then to be printed to terminal, so we get feedback if it worked or not
                    if (response.equals("ok")){
                        System.out.println("Task solved");
                    } else {
                        System.out.println("ERROR: Task not solved");
                    }
                }
            }
        }
    }

    /**
     * Wait for a response from the UDP task server
     *
     * @return returns true (successful) or false based on success
     */
    private String listenForResponse() {

        String response = null;

        byte[] responseDataBuffer = new byte[1024]; // Reserve a bit more space than one would normally need
        DatagramPacket receivePacket = new DatagramPacket(responseDataBuffer, responseDataBuffer.length);
        try {
            this.socket.receive(receivePacket);
            response = new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
        return response;
    }

    /**
     * Sends a message to server based on UDP
     * @param message Message to be sent
     *
     * @return returns true (successful) or false based on success
     */
    private boolean sendToServer(String message) {
        boolean success = false;

        byte[] dataToSend = message.getBytes();
        try {
            InetAddress serverAddress = InetAddress.getByName("129.241.152.12");
            DatagramPacket sendPacket = new DatagramPacket(dataToSend, dataToSend.length, serverAddress, 1234);
            this.socket.send(sendPacket);
            if (message.equals("task")){
                success = true;
            }
        } catch (Exception e){
            System.out.println("Could not be sent to server" + e.getMessage());
            throw new RuntimeException(e);
        }
       return success;
    }

    /**
     * Solving a task given by the UDP server
     *
     * @param task A task (sentence)
     * @return The answer according to the protocol: sentenceType wordCount
     */
    public static String solveTaskResponseFromServer(String task) {
        String type = "";

        // Finds the sentence type
        if (task.endsWith(".")){
            type = "statement";
        } else if (task.endsWith("?")){
            type = "question";
        }
        int wordCount = 0;

        // splits the string for every space detected and then puts them in an array then returns the length
        wordCount = task.split(" ").length;

        // If one of these conditions are true. wordCount = 0
        if (task.isEmpty() || task.length() == 1){
            wordCount = 0;
        }
        // Connects the final string and returns it
        return type + " " + wordCount;
    }
}