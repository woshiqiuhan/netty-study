package cn.hznu.chatroom.server;

public class ServerMain {
    public static void main(String[] args) {
        try {
            new Server(9584).run();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}