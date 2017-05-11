package serialization;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * 接收序列化对象-客户端
 * 
 * @author zhminchao@163.com
 * @date 2017年5月5日 下午2:32:37
 */
public class Client {

	public static void main(String[] args) throws Exception {

		new Client("localhost", 8082).start();
	}

	private final String host;
	private final int port;

	/**
	 * 客户端待消费队列
	 */
	static ConcurrentLinkedQueue<SerializationBean> taskQueue = new ConcurrentLinkedQueue<SerializationBean>();

	public Client(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * 通信用的channel
	 */
	public Channel channel;
	/**
	 * 启动器
	 */
	private Bootstrap bootstrap; 
	
	/**
	 * 向服务端建立连接的方法。建立成功会将全局的channel赋值为新的channel。失败会每五秒重新链接
	 */
	protected void doConnect() {

		if (channel != null && channel.isActive()) {
			return;
		}

		ChannelFuture future = bootstrap.connect(host, port);

		future.addListener(new ChannelFutureListener() {
			public void operationComplete(ChannelFuture futureListener) throws Exception {
				if (futureListener.isSuccess()) {
					channel = futureListener.channel();
					System.out.println("Connect to server successfully!");
				} else {
					System.out.println("Failed to connect to server, try connect after 5s");

					futureListener.channel().eventLoop().schedule(new Runnable() {
						@Override
						public void run() {
							doConnect();
						}
					}, 5, TimeUnit.SECONDS);
				}
			}
		});
	}

	/**
	 * 客户端运行持续运行
	 */
	public void start() {
		
		startConsume();
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap = new Bootstrap()
							.group(group)
							.channel(NioSocketChannel.class)
							.handler(new ClientHandlerInitializer(this));
			doConnect();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 同时启动客户端消费者线程和轮询线程
	 */
	void startConsume() {
		// 启动客户端消费者线程
		new Consumer(taskQueue).start();
		// 启动轮询线程
		new ClientPolling(this).start();
	}
}
