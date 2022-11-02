package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {
    private final String id;

    public ChatClient(String id) {
        this.id = id;
    }

    public static void main(String[] args) {
        if (hasNotArgs(args)) {
            System.out.println("USAGE: java MyChatClient {id}");
            return;
        }
        String id = args[0];
        ChatClient client = new ChatClient(id);
        client.connect("192.168.0.109", 8888);
    }

    private static boolean hasNotArgs(String[] args) {
        return args.length == 0;
    }

    private void connect(String severHost, int port) {
        try {
            Socket socket = new Socket(severHost, port);
            System.out.println("Connected to Server " + severHost + ":" + port);
            Thread sender = new Sender(socket, id);
            Thread receiver = new Receiver(socket);

            sender.start();
            receiver.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Sender extends Thread {
        private String id;
        private DataOutputStream out;

        private Sender(Socket socket, String id) throws IOException {
            this.id = id;
            this.out = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                initialize();
                sendMessage();
            } catch (IOException e) {
                // TODO
            }
        }

        private void initialize() throws IOException {
            if (isSendAble()) {
                this.out.writeUTF(id);
            }
        }

        private boolean isSendAble() {
            return this.out != null;
        }

        private void sendMessage() throws IOException {
            try (Scanner scanner = new Scanner(System.in)) {
                while (isSendAble()) {
                    this.out.writeUTF("[" + id + "]" + scanner.nextLine());
                }
            }
        }
    }

    private static class Receiver extends Thread {
        private final DataInputStream in;

        private Receiver(Socket socket) throws IOException {
            this.in = new DataInputStream(socket.getInputStream());
        }

        @Override
        public void run() {
            while (isReceivable()) {
                receiveMessage();
            }
        }

        private boolean isReceivable() {
            return this.in != null;
        }

        private void receiveMessage() {
            try {
                System.out.println(in.readUTF());
            } catch (IOException e) {
                // TODO
            }
        }
    }

}
