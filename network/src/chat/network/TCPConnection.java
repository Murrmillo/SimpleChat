package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection { //TCP соединение

    private  final Socket socket; //сокет соединения
    private  final Thread rxThread; //поток слушаший, входящие соединения
    private  final BufferedReader in;
    private  final BufferedWriter out;
    private  final TCPConnectionListener evenListener; //слушатель событый

    public TCPConnection(TCPConnectionListener evenListener, String ipAddr, int port) throws IOException
    {
        this(evenListener,new Socket(ipAddr,port));
    }

    public  TCPConnection(TCPConnectionListener evenListener, Socket socket) throws IOException
    {
        this.evenListener=evenListener;
        this.socket=socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter((new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8"))));
        rxThread = new Thread(new Runnable() { //описываем класс, который реализует интерфейс Runnable,
                                               // переопределяем у него метод Run и создаем его экземпляр
            @Override
            public void run() {
                try
                {
                    evenListener.onConnectionReady(TCPConnection.this); //передаем экземпляр обромляющего класса
                    while(!rxThread.isInterrupted()) //пока поток не прерван
                    {
                        evenListener.onReceiveString(TCPConnection.this,in.readLine()); //получаем строку и передаем ее EventListener
                    }
                }
                catch(IOException e)
                {
                    evenListener.onException(TCPConnection.this,e);
                    disconnect();
                }
                finally
                {
                    evenListener.onDisconnect(TCPConnection.this);

                }





            }
        });
        rxThread.start();
    }


    public synchronized  void sendString(String value) //метод отправки сообщения
    {
        try
        {
            out.write(value+"\r\n"); //добавление признака конца строки
            out.flush(); //принудительно сбросить буфер
        }
        catch (IOException e)
        {
            evenListener.onException(TCPConnection.this, e);
            disconnect();
        }

    }

    public synchronized  void disconnect() //метод обрыва соединения
    {
        rxThread.interrupt(); //прерывание потока
        try {
            socket.close(); //закрытие сокета
        }catch (IOException e)
        {
            evenListener.onException(TCPConnection.this,e);
        }

    }

    @Override
    public String toString()
    {   //переопределение метода toString
        return "TCPConnection: " + socket.getInetAddress()+": " + socket.getPort();
    }


}
