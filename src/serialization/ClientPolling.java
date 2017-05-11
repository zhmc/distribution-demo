package serialization;

import io.netty.channel.Channel;

/**
 * 轮询线程，不断地向服务端请求新的任务
 * 
 * @author zhminchao@163.com
 * @date 2017年5月5日 上午10:44:23
 */

public class ClientPolling extends Thread {
	
	private Client client;
	/**
	 * 服务端暂时没任务，需要等待的标志。用共享变量来保持状态
	 */
	public static boolean waitFlag = false;
	// public static volatile boolean waitFlag = false;

	public ClientPolling(Client client) {
		this.client=client;
	}

	@Override
	public void run() {
		loop2();

	}

	/**
	 * 轮流循环查询方法1 。主动请求，直达服务端返回没有。暂停5s，继续请求
	 * 
	 * @author zhminchao@163.com
	 * @date 2017年5月5日 下午2:53:43
	 */
	void loop1() {
		while (true) {
			try {

				if (!waitFlag) {
					// 不加上这个sleep就会很奇怪，run模式不能正确运行，debug模式却能够正确运行
					//经过方法2的使用发现，原因是debug模式会停顿，等同于sleep。
					//最终发现原因是因为客户端的handler工作方式是异步的。也就是当这边向服务端请求更多任务时，
					//等到客户端接受到服务端返回的NO_TASK消息，可能是5ms之后了，在这5ms之间，如果一点停顿都
					//没有，客户端就会发出超级多新请求，一直等待在buffer区，直接就把内存占满了
//					Thread.sleep(1);
					System.out.println("");
					client.channel.writeAndFlush(SignalList.NEED_TASK);
				} else {
					Thread.sleep(1000 * 5);
					waitFlag = false;
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 轮流循环查询方法2 。当客户端的队列为空时，向服务端主动请求。直达服务端返回没有。暂停5s，继续请求
	 * 这个方法可以不再考虑负载均衡，因为是消费完了才去请求新的任务
	 * @author zhminchao@163.com
	 * @date 2017年5月5日 下午2:53:43
	 */
	void loop2() {
		while (true) {
			
			//因为client掉线后，再重新连接后client.channel会改变。需要重新传递指针
			Channel channel = client.channel;

			//刚开始连接未建立时，client.channel为空指针
			if(channel==null){
				try {
					Thread.sleep(1000 * 1);
					continue;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
//				if (!Client.taskQueue.isEmpty()) {
				if (Client.taskQueue.size()>10) {
					Thread.sleep(1000 * 1);
				}else {
					if (!waitFlag) {
						// 不加上这个sleep就会很奇怪，run模式不能正确运行，debug模式却能够正确运行
						//经过方法2的使用发现，原因是debug模式会停顿，等同于sleep。 TODO 为什么需要停顿
						Thread.sleep(1);
						channel.writeAndFlush(SignalList.NEED_TASK);
					} else {
						Thread.sleep(1000 * 5);
						waitFlag = false;
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}