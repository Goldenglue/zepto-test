package org.goldenglue;

import org.goldenglue.network.Server;

public class Runner {
    public static void main(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException("Program requires two args: <port> <number of messages to send on join>");
        }

        new Server(Integer.parseInt(args[0]), 10, Integer.parseInt(args[1])).start();
        System.out.println("Started");
    }
}
