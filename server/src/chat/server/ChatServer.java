package chat.server;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


public class ChatServer implements TCPConnectionListener {

    public static void main(String[] args) {
        new ChatServer();

    }

    private  final ArrayList<TCPConnection> connections = new ArrayList<>(); //список TCP соединений

    private ChatServer()
    {
        System.out.println("Server running...");
        try
        {
            ServerSocket serverSocket = new ServerSocket(8001); //создание СерверСокета, который слушает TCP порт 8001
            while (true) //беспонечный цикл
            {
                try
                {
                    new TCPConnection(this,serverSocket.accept()); //в бесконечном цикле в методе accept,
                                                                               //который ждет нового соединения и при установлении возвращает обэект сокета,
                                                                               //который связан с этим соединением
                                                                               //передаем его в конструктор TCPConnection, включая себя как листенера
                                                                               //и создаем его экземпляр
                }
                catch (IOException e)
                {
                    System.out.println("TCPConnection exception; "+ e);
                }
            }
        }
        catch (IOException e)
        {
            throw  new RuntimeException(e);
        }
    }

    @Override
    public synchronized void onConnectionReady(TCPConnection tcpConnection) {
        connections.add(tcpConnection); //добавление соендинения в список
        sendToAllConnections("Client connected " + tcpConnection);
    }

    @Override
    public synchronized void onReceiveString(TCPConnection tcpConnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void onDisconnect(TCPConnection tcpConnection) {
        connections.remove(tcpConnection); //добавление соендинения из списка
        sendToAllConnections("Client disconnected " + tcpConnection);
    }

    @Override
    public synchronized void onException(TCPConnection tcpConnection, Exception e) {
        System.out.println("TCPConnection exception " + e);
    }

    private void sendToAllConnections(String value) //метод, для того, чтобы разсылать сообщения всем клиентам
    {
        System.out.println(value);
        final int count = connections.size();
        for (int i =0;i<count;i++)
        {
            connections.get(i).sendString(value);
        }
    }
}
