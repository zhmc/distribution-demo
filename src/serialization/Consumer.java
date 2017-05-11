package serialization;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 客户端消费者线程
 * 在这个地方扩展新的需要
 * @author zhminchao@163.com
 * @date 2017年5月5日 下午2:42:24
 */
public class Consumer extends Thread {
	ConcurrentLinkedQueue<SerializationBean> taskQueue;

	public Consumer(ConcurrentLinkedQueue<SerializationBean> taskQueue) {
		this.taskQueue = taskQueue;
	}

	@Override
	public void run() {
		
		while (true) {
			try {
				if (taskQueue.isEmpty()) {

					Thread.sleep(1000);

				}else {
					SerializationBean task = taskQueue.poll();
					
					//假定处理业务需要100ms
					Thread.sleep(100);
					System.out.println("消费了一个task");
					System.out.println(task);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
