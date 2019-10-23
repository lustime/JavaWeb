package io.github.dunwu.javaweb.zookeeper;

import java.util.List;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZKGetChildren {

	private static final String HOST = "localhost";

	private static ZooKeeper zk;

	private static ZKConnection conn;

	public static void main(String[] args) throws InterruptedException, KeeperException {
		String path = "/MyFirstZnode"; // Assign path to the znode

		try {
			conn = new ZKConnection();
			zk = conn.connect(HOST);
			Stat stat = znode_exists(path); // Stat checks the path

			if (stat != null) {
				// getChildren method - get all the children of znode.It has two args,
				// path and watch
				List<String> children = zk.getChildren(path, false);
				for (int i = 0; i < children.size(); i++) {
					System.out.println(children.get(i)); // Print children's
				}
			} else {
				System.out.println("Node does not exists");
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	// Method to check existence of znode and its status, if znode is available.
	public static Stat znode_exists(String path) throws KeeperException, InterruptedException {
		return zk.exists(path, true);
	}

}
