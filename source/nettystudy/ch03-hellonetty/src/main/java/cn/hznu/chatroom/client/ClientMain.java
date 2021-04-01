package cn.hznu.chatroom.client;

public class ClientMain {
    public static void main(String[] args) {
        try {
            new Client("localhost", 9584).connect();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
