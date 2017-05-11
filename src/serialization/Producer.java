package serialization;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 服务端生产者线程，不断制作bean，添加到队列中
 * 在这个地方扩展，适应新的需要
 * 
 * @author zhminchao@163.com
 * @date 2017年5月5日 上午10:04:01
 */
public class Producer extends Thread {
	ConcurrentLinkedQueue<SerializationBean> taskQueue;

	public Producer(ConcurrentLinkedQueue<SerializationBean> taskQueue) {
		this.taskQueue = taskQueue;
	}

	public void run() {
		for(int i=0;;i++){
			SerializationBean user = new SerializationBean();
			user.setName("waylau"+i);
			user.setAge(System.currentTimeMillis());
			
			taskQueue.add(user);
			System.out.println("生产了一个task");
			System.out.println(user);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}

}
