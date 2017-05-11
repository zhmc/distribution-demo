package serialization;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * 服务端handler
 * @author Administrator
 *
 */
public class ServerHandler extends SimpleChannelInboundHandler<Object> {
 
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object obj)
			throws Exception {
		
		if(obj instanceof String){
			String msg = (String) obj;
			if(SignalList.NEED_TASK.equals(msg)){
				if(Server.taskQueue.isEmpty()){
					ctx.writeAndFlush(SignalList.NO_TASK);
				} else {
					SerializationBean user = Server.taskQueue.poll();
					ctx.writeAndFlush(user);
				}
				
			}
			return;
		}
		
		if (obj instanceof SerializationBean) {
			SerializationBean user = (SerializationBean)obj;
			System.out.println("Server get msg form Client - name:"+ user.getName() + ";age:" + user.getAge());
			//写回去
			user.setName(user.getName()+"已读取");
			ctx.writeAndFlush(user);
		}
	}

}
