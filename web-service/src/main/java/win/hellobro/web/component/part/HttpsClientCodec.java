package win.hellobro.web.component.part;

import io.netty.channel.ChannelHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContextBuilder;
import team.balam.exof.module.listener.handler.ChannelHandlerMaker;

import javax.net.ssl.SSLException;

public class HttpsClientCodec implements ChannelHandlerMaker {
	@Override
	public ChannelHandler[] make(SocketChannel socketChannel) {
		try {
			return new ChannelHandler[]{
					SslContextBuilder.forClient().build().newHandler(socketChannel.alloc()),
					new io.netty.handler.codec.http.HttpClientCodec(),
					new HttpObjectAggregator(Integer.MAX_VALUE)};
		} catch (SSLException e) {
			throw new RuntimeException(e);
		}
	}
}
