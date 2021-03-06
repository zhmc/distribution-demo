package serialization;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 * 客户端处理器初始化
 *
 */
public class ClientHandlerInitializer extends ChannelInitializer<Channel> {

	final static int MAX_OBJECT_SIZE = 1024 * 1024;
	Client client;

	ClientHandlerInitializer(Client client){
		this.client=client;
	}
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new ObjectDecoder(MAX_OBJECT_SIZE,
				ClassResolvers.weakCachingConcurrentResolver(this.getClass()
						.getClassLoader())));
		pipeline.addLast(new ObjectEncoder());
		pipeline.addLast(new ClientHandler(client));
	}
}