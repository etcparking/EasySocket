package com.easysocket;

import android.content.Context;

import com.easysocket.config.EasySocketOptions;
import com.easysocket.connection.heartbeat.HeartManager;
import com.easysocket.entity.SocketAddress;
import com.easysocket.entity.basemsg.SuperCallbackSender;
import com.easysocket.exception.InitialExeption;
import com.easysocket.exception.NotNullException;
import com.easysocket.interfaces.conn.IConnectionManager;
import com.easysocket.interfaces.conn.ISocketActionListener;

/**
 * Author：Alex
 * Date：2019/6/4
 * Note：EasySocket API
 */
public class EasySocket {

    /**
     * 连接的缓存
     */
    private static ConnectionHolder connectionHolder = ConnectionHolder.getInstance();
    // 单例
    private volatile static EasySocket singleton = null;
    /**
     * 连接参数
     */
    private EasySocketOptions options;
    /**
     * 连接器
     */
    private IConnectionManager connection;
    /**
     * 上下文
     */
    private Context context;

    /**
     * 单例
     *
     * @return
     */
    public static EasySocket getInstance() {
        if (singleton == null) {
            synchronized (EasySocket.class) {
                if (singleton == null) {
                    singleton = new EasySocket();
                }
            }
        }
        return singleton;
    }

    /**
     * 设置连接参数
     */
    public EasySocket options(EasySocketOptions socketOptions) {
        options = socketOptions;
        return this;
    }

    /**
     * 获取上下文
     *
     * @return
     */
    public Context getContext() {
        return context;
    }

    /**
     * 获取配置参数
     *
     * @return
     */
    public EasySocketOptions getOptions() {
        return options == null ? EasySocketOptions.getDefaultOptions() : options;
    }

    /**
     * 创建连接
     *
     * @return
     */
    public EasySocket createConnection(Context context) {
        this.context = context;
        SocketAddress socketAddress = options.getSocketAddress();
        if (options.getSocketAddress() == null) {
            throw new InitialExeption("请在初始化的时候设置SocketAddress");
        }
        // 如果有备用主机则设置
        if (options.getBackupAddress() != null) {
            socketAddress.setBackupAddress(options.getBackupAddress());
        }
        if (connection == null) {
            connection = connectionHolder.getConnection(socketAddress,
                    options == null ? EasySocketOptions.getDefaultOptions() : options);
        }
        // 执行连接
        connection.connect();
        return this;
    }

    /**
     * 连接socket
     *
     * @return
     */
    public EasySocket connect() {
        getConnection().connect();
        return this;
    }

    /**
     * 关闭连接
     *
     * @param isNeedReconnect 是否需要重连
     * @return
     */
    public EasySocket disconnect(boolean isNeedReconnect) {
        getConnection().disconnect(isNeedReconnect);
        return this;
    }

    /**
     * 销毁连接对象
     *
     * @return
     */
    public EasySocket destroyConnection() {
        // 断开连接
        getConnection().disconnect(false);
        // 移除连接
        connectionHolder.removeConnection(options.getSocketAddress());
        connection = null;
        return this;
    }

    /**
     * 发送有回调的消息
     *
     * @param sender
     * @return
     */
    public IConnectionManager upCallbackMessage(SuperCallbackSender sender) {
        getConnection().upCallbackMessage(sender);
        return connection;
    }

    /**
     * 发送byte[]
     *
     * @param bytes
     * @return
     */
    public IConnectionManager upMessage(byte[] bytes) {
        getConnection().upBytes(bytes);
        return connection;
    }

    /**
     * 注册监听socket行为
     *
     * @param socketActionListener
     */
    public EasySocket subscribeSocketAction(ISocketActionListener socketActionListener) {
        getConnection().subscribeSocketAction(socketActionListener);
        return this;
    }

    /**
     * 开启心跳检测
     *
     * @param clientHeart
     * @return
     */
    public EasySocket startHeartBeat(byte[] clientHeart, HeartManager.HeartbeatListener listener) {
        getConnection().getHeartManager().startHeartbeat(clientHeart, listener);
        return this;
    }


    /**
     * 获取连接
     *
     * @return
     */
    public IConnectionManager getConnection() {
        if (connection == null) {
            throw new NotNullException("请先创建socket连接");
        }
        return connection;
    }

    /**
     * 创建指定的连接
     *
     * @param socketAddress
     * @param socketOptions
     * @return
     */
    public IConnectionManager buildSpecifyConnection(SocketAddress socketAddress, EasySocketOptions socketOptions) {
        IConnectionManager connectionManager = connectionHolder.getConnection(socketAddress, socketOptions == null
                ? EasySocketOptions.getDefaultOptions() : socketOptions);
        connectionManager.connect();
        return connectionManager;
    }

    /**
     * 获取指定的连接
     *
     * @param socketAddress
     * @return
     */
    public IConnectionManager getSpecifyConnection(SocketAddress socketAddress) {
        return connectionHolder.getConnection(socketAddress);
    }

    /**
     * 发送消息至指定的连接
     *
     * @param sender
     * @param socketAddress
     */
    public IConnectionManager upToSpecifyConnection(byte[] sender, SocketAddress socketAddress) {
        IConnectionManager connect = getSpecifyConnection(socketAddress);
        if (connect != null) {
            connect.upBytes(sender);
        }
        return connect;
    }

    /**
     * 是否为debug
     *
     * @param debug
     */
    public void setDebug(boolean debug) {
        EasySocketOptions.setIsDebug(debug);
    }

}
