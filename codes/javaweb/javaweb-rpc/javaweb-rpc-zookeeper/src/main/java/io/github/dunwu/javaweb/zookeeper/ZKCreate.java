package io.github.dunwu.javaweb.zookeeper;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

/**
 * ZooKeeper 添加 Znode 示例
 */
public class ZKCreate {

	private static final String HOST = "localhost";
	// create static instance for zookeeper class.
	private static ZooKeeper zk;
	// create static instance for ZooKeeperConnection class.
	private static ZKConnection conn;

	// Method to create znode in zookeeper ensemble
	public static void create(String path, byte[] data) throws KeeperException, InterruptedException {
		zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
	}

	public static void main(String[] args) throws InterruptedException {

		// znode path
		String path = "/MyFirstZnode"; // Assign path to znode

		// data in byte array
		byte[] data = "My first zookeeper app".getBytes(); // Declare data

		try {
			conn = new ZKConnection();
			zk = conn.connect(HOST);
			create(path, data); // Create the data to the specified path
		}
		catch (Exception e) {
			System.out.println(e.getMessage()); // Catch error message
		}
		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

}
