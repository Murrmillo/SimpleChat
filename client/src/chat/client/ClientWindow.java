package chat.client;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

    private  static final String IP_ADDR="127.0.0.1";
    private static final int PORT=8001;
    private static final int WIDTH=600;
    private static final int HEIGHT=400;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() { //"заставляет" выполняться в потоке EDT(Event dispatching thread)
            @Override
            public void run() {
                new ClientWindow();
            }
        });
    }

    private  final JTextArea log = new JTextArea(); //поле для отображения сообщений
    private final JTextField fieldNickname = new JTextField("User"); //однострочное поле для ввода имени пользователя
    private  final JTextField fieldInput = new JTextField(); //однострочное поле для ввода сообщения

    private TCPConnection connection;

    private  ClientWindow()
    {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //закрытие программы на крестик
        setSize(WIDTH,HEIGHT);
        setLocationRelativeTo(null); //окно чата всегда по центру
        setAlwaysOnTop(true); //окно чата всегда сверху

        log.setEditable(false);
        log.setLineWrap(true);



        add(log, BorderLayout.CENTER);
        add(fieldNickname,BorderLayout.NORTH);
        add(fieldInput,BorderLayout.SOUTH);
        fieldInput.addActionListener(this);

        setVisible(true);
        try {
            connection = new TCPConnection(this,IP_ADDR,PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) //ловим нажатие на клавишу Enter
    {
        String msg = fieldInput.getText();
        if (msg.equals("")) return;
        fieldInput.setText(null);
        connection.sendString(fieldNickname.getText()+": " + msg);
    }


    @Override
    public void onConnectionReady(TCPConnection tcpConnection) {
        printMessage("Connection ready...");
    }

    @Override
    public void onReceiveString(TCPConnection tcpConnection, String value) {
        printMessage(value);
    }

    @Override
    public void onDisconnect(TCPConnection tcpConnection) {
        printMessage("Connection close...");
    }

    @Override
    public void onException(TCPConnection tcpConnection, Exception e) {
        printMessage("Connection exception " + e);
    }

    private synchronized void printMessage(String msg)
    {
        SwingUtilities.invokeLater(new Runnable() {  //"заставляет" выполняться в потоке EDT(Event dispatching thread)
            @Override
            public void run() {
                log.append(msg+"\n"); //добавление строчки и перехода на следующую
                log.setCaretPosition(log.getDocument().getLength()); //установление каретки в самый конец
            }
        });
    }
}
