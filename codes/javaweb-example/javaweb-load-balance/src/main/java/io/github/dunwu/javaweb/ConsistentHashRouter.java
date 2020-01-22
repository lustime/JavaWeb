// /*
//  * Licensed to the Apache Software Foundation (ASF) under one or more
//  * contributor license agreements.  See the NOTICE file distributed with
//  * this work for additional information regarding copyright ownership.
//  * The ASF licenses this file to You under the Apache License, Version 2.0
//  * (the "License"); you may not use this file except in compliance with
//  * the License.  You may obtain a copy of the License at
//  *
//  *     http://www.apache.org/licenses/LICENSE-2.0
//  *
//  * Unless required by applicable law or agreed to in writing, software
//  * distributed under the License is distributed on an "AS IS" BASIS,
//  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  * See the License for the specific language governing permissions and
//  * limitations under the License.
//  */
// package io.github.dunwu.javaweb;
//
// import java.security.MessageDigest;
// import java.security.NoSuchAlgorithmException;
// import java.util.Collection;
// import java.util.Iterator;
// import java.util.SortedMap;
// import java.util.TreeMap;
//
// /**
//  * @param <T>
//  * @author linjunjie1103@gmail.com
//  * <p>
//  * To hash Node objects to a hash ring with a certain amount of virtual node. Method routeNode will return a Node
//  * instance which the object key should be allocated to according to consistent hash algorithm
//  */
// public class ConsistentHashRouter<T extends Node> {
//
//     private final static int VIRTUAL_NODE_SIZE = 10;
//
//     private final SortedMap<Long, VirtualNode<T>> hashRing = new TreeMap<>();
//
//     private final HashStrategy hashStrategy;
//
//     public ConsistentHashRouter(Collection<T> physicalNodes, int count) {
//         this(physicalNodes, count, new MD5Hash());
//     }
//
//     /**
//      * @param nodes        collections of physical nodes
//      * @param count        amounts of virtual nodes
//      * @param hashStrategy hash Function to hash Node instances
//      */
//     public ConsistentHashRouter(Collection<T> nodes, int count, HashStrategy hashStrategy) {
//         if (hashStrategy == null) {
//             throw new NullPointerException("Hash Function is null");
//         }
//         this.hashStrategy = hashStrategy;
//         if (nodes != null) {
//             for (T node : nodes) {
//                 addNode(node, count);
//             }
//         }
//     }
//
//     /**
//      * add physic node to the hash ring with some virtual nodes
//      *
//      * @param node  physical node needs added to hash ring
//      * @param count the number of virtual node of the physical node. Value should be greater than or equals to 0
//      */
//     public void addNode(T node, int count) {
//         if (count < 0) throw new IllegalArgumentException("illegal virtual node counts :" + count);
//         int existingReplicas = getExistingReplicas(node);
//         for (int i = 0; i < count; i++) {
//             VirtualNode<T> nodes = new VirtualNode<>(node, i + existingReplicas);
//             hashRing.put(hashStrategy.hash(nodes.getKey()), nodes);
//         }
//     }
//
//     /**
//      * remove the physical node from the hash ring
//      *
//      * @param node
//      */
//     public void removeNode(T node) {
//         Iterator<Long> it = hashRing.keySet().iterator();
//         while (it.hasNext()) {
//             Long key = it.next();
//             VirtualNode<T> virtualNode = hashRing.get(key);
//             if (virtualNode.isVirtualNodeOf(node)) {
//                 it.remove();
//             }
//         }
//     }
//
//     /**
//      * with a specified key, route the nearest Node instance in the current hash ring
//      *
//      * @param objectKey the object key to find a nearest Node
//      * @return
//      */
//     public T next(String objectKey) {
//         if (hashRing.isEmpty()) return null;
//         long hashcode = hashStrategy.hash(objectKey);
//         SortedMap<Long, VirtualNode<T>> tailMap = hashRing.tailMap(hashcode);
//         Long nodeHashVal = !tailMap.isEmpty() ? tailMap.firstKey() : hashRing.firstKey();
//         return hashRing.get(nodeHashVal).getPhysicalNode();
//     }
//
//     public int getExistingReplicas(T pNode) {
//         int replicas = 0;
//         for (VirtualNode<T> node : hashRing.values()) {
//             if (node.isVirtualNodeOf(pNode)) {
//                 replicas++;
//             }
//         }
//         return replicas;
//     }
//
//     //default hash function
//     static class MD5Hash implements HashStrategy {
//
//         MessageDigest instance;
//
//         public MD5Hash() {
//             try {
//                 instance = MessageDigest.getInstance("MD5");
//             } catch (NoSuchAlgorithmException e) {
//                 e.printStackTrace();
//             }
//         }
//
//         @Override
//         public long hash(String key) {
//             instance.reset();
//             instance.update(key.getBytes());
//             byte[] digest = instance.digest();
//
//             long h = 0;
//             for (int i = 0; i < 4; i++) {
//                 h <<= 8;
//                 h |= ((int) digest[i]) & 0xFF;
//             }
//             return h;
//         }
//
//     }
//
//     static class VirtualNode<T extends Node> implements Node {
//
//         final T physicalNode;
//
//         final int replicaIndex;
//
//         public VirtualNode(T physicalNode, int replicaIndex) {
//             this.replicaIndex = replicaIndex;
//             this.physicalNode = physicalNode;
//         }
//
//         @Override
//         public String getKey() {
//             return physicalNode.getKey() + "-" + replicaIndex;
//         }
//
//         public boolean isVirtualNodeOf(T pNode) {
//             return physicalNode.getKey().equals(pNode.getKey());
//         }
//
//         public T getPhysicalNode() {
//             return physicalNode;
//         }
//
//     }
//
// }
