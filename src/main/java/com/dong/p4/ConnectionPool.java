package com.dong.p4;

import utils.SleepUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.LinkedList;

/**
 * 4.4.2 一个简单的数据库连接池示例
 */
public class ConnectionPool {
    private LinkedList<Connection> connectionPool = new LinkedList<>();

    public ConnectionPool(int initialSize) {
        for (int i = 0; i < initialSize; i++) {
            connectionPool.add(ConnectionDriver.createConnection());
        }
    }

    /**
     * 释放连接
     */
    public void releaseConnection(Connection connection) {
        if (connection != null) {
            synchronized (connectionPool) {
                connectionPool.addLast(connection);
                // 添加连接后通知其他消费者可以使用该连接
                connectionPool.notifyAll();
            }
        }
    }

    /**
     * 抓取连接
     *
     * @param mills 超时控制，单位毫秒
     * @throws InterruptedException
     */
    public Connection fetchConnection(long mills) throws InterruptedException {
        long future = System.currentTimeMillis() + mills;
        long remained = future - System.currentTimeMillis();
        synchronized (connectionPool) {
            // 检测连接池是否为空
            // 检测是否超时
            if (connectionPool == null || mills <= 0) {
                return null;
            }

            // 获取连接
            while (connectionPool.isEmpty() && remained > 0) {
                // 等待，加入了超时控制
                connectionPool.wait(remained);
                remained = future - System.currentTimeMillis();
            }

            Connection result = null;
            if (!connectionPool.isEmpty()) {
                result = connectionPool.removeFirst();
            }

            return result;
        }
    }

}

/**
 * 数据库驱动mock，模拟生成数据库连接
 */
class ConnectionDriver {
    static class ConnectionHandler implements InvocationHandler {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if ("createStatement".equals(method.getName())) {
//                System.out.println("创建statement:" + new Date());
            } else if ("commit".equals(method.getName())) {
                SleepUtils.millSecond(100);
//                System.out.println("事务提交成功, timestamp:" + new Date());
            }
            return null;
        }
    }

    public static final Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(ConnectionHandler.class.getClassLoader(), new Class[]{Connection.class}, new ConnectionHandler());
    }
}