package io.github.dunwu.javaweb.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

public class ZKDelete {
    private static ZooKeeper zk;
    private static ZKConnection conn;
    private static final String HOST = "localhost";

    // Method to check existence of znode and its status, if znode is available.
    public static void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path, zk.exists(path, true).getVersion());
    }

    public static void main(String[] args) throws InterruptedException, KeeperException {
        String path = "/MyFirstZnode"; //Assign path to the znode

        try {
            conn = new ZKConnection();
            zk = conn.connect(HOST);
            delete(path); //delete the node with the specified path
        } catch (Exception e) {
            System.out.println(e.getMessage()); // catches error messages
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
