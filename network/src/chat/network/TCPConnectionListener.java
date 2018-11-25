package chat.network;

public interface TCPConnectionListener
{
    void onConnectionReady(TCPConnection tcpConnection); //соединения установлено
    void onReceiveString(TCPConnection tcpConnection, String value); //строчка принята
    void onDisconnect(TCPConnection tcpConnection); //соединение оборвалось
    void onException(TCPConnection tcpConnection,Exception e); //случилось исключение
}
