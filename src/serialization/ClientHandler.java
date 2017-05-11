/**
 * 
 */
package serialization;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 客户端处理handler
 * 
 * @author zhminchao@163.com
 * @date 2017年5月5日 下午2:41:51
 */
public class ClientHandler extends SimpleChannelInboundHandler<Object> {
	private Client client;

	public ClientHandler(Client client) {
		this.client = client;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {

		if (obj instanceof String) {
			String msg = (String) obj;
			if (msg.equals(SignalList.NO_TASK)) {
				ClientPolling.waitFlag = true;
				System.out.println("客户端收到服务端暂时没有任务的信息");
			}

			return;
		}

		if (obj instanceof SerializationBean) {
			SerializationBean user = (SerializationBean) obj;
			System.out.println("Client get msg form Server - name:" + user.getName() + ";age:" + user.getAge());
			Client.taskQueue.add(user);
		}
	}


	/**
	 * handler的断线重连方法
	 * @see io.netty.channel.ChannelInboundHandlerAdapter#channelInactive(io.netty.channel.ChannelHandlerContext)
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {

		super.channelInactive(ctx);
		client.doConnect();
	}
}
