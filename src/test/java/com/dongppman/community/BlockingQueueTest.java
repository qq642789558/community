package com.dongppman.community;


import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *阻塞队列,
 */

public class BlockingQueueTest {
    public static void main(String[] args) {
        BlockingQueue queue=new ArrayBlockingQueue(10);
        new Thread(new Producer(queue)).start();
        new Thread(new customer(queue)).start();
        new Thread(new customer(queue)).start();
        new Thread(new customer(queue)).start();
    }
}


class Producer implements Runnable{
    private BlockingQueue<Integer> queue;
    public Producer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }
    @Override
    public void run() {

        for (int i = 0; i < 100; i++) {
            try {
                Thread.sleep(20);
                queue.put(i);
                System.out.println(Thread.currentThread().getName()+"生产"+queue.size());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

class customer implements Runnable{
    private BlockingQueue<Integer> queue;

    public customer(BlockingQueue<Integer> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {
        try {
            while (true){
                Thread.sleep(new Random().nextInt(1000));
                queue.take();
                System.out.println(Thread.currentThread().getName()+"消费"+queue.size());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


/**
 * kafka:分布式的流媒体平台
 * 特点:高吞吐量,消息持久化(存到硬盘上),硬盘的顺序读取速度是很快的,
 * 术语:
 * Broker:kafka的服务器
 * Zookeeper:管理其他的集群
 * Topic:生产者发布消息的位置
 * Partition:topic的分区,
 * offset消息在分区的索引
 * replica:副本,
 */