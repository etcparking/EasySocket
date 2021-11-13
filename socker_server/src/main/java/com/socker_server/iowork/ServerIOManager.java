package com.socker_server.iowork;

import com.socker_server.HandlerIO;

import java.io.IOException;
import java.net.Socket;

/**
 * Author Alex
 * update by jambestwick
 * Date 2019/5/28
 * Note SocketServer IO
 */
public class ServerIOManager implements IIOManager {

    /**
     * ioд
     */
    private IWriter writer;
    /**
     * io��
     */
    private IReader reader;

    public ServerIOManager(Socket socket) {
        try {
            initIO(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //初始化IO
    private void initIO(Socket socket) throws IOException {
        writer = new ServerWriter(socket.getOutputStream(), socket); //д
        HandlerIO handlerIO = new HandlerIO(writer);
        reader = new ServerReader(socket.getInputStream(), socket, handlerIO); //��
    }

    @Override
    public void sendBuffer(byte[] buffer) {
        if (writer != null)
            writer.offer(buffer);
    }

    @Override
    public void startIO() {
        if (writer != null)
            writer.openWriter();
        if (reader != null)
            reader.openReader();
    }

    @Override
    public void closeIO() {
        if (writer != null)
            writer.closeWriter();
        if (reader != null)
            reader.closeReader();
    }

//    /**
//     * 判断socket包头不为空
//     */
//    private void makesureHeaderProtocolNotEmpty() {
//        IReaderProtocol protocol = connectionManager.getOptions().getReaderProtocol();
//        if (protocol == null) {
//            throw new NoNullExeption("The reader protocol can not be Null.");
//        }
//
//        if (protocol.getHeaderLength() == 0) {
//            throw new NoNullExeption("The header length can not be zero.");
//        }
//    }
}
