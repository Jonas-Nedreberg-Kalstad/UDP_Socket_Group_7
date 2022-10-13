package no.ntnu.gruppe7;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        UdpSocketHandler sender = new UdpSocketHandler();
        sender.run();
    }
}