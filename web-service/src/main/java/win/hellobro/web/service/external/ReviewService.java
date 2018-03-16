package win.hellobro.web.service.external;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.Client;
import team.balam.exof.client.ClientPool;
import team.balam.exof.client.component.HttpClientCodec;
import team.balam.exof.module.service.annotation.Service;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;

import java.nio.charset.Charset;

@ServiceDirectory
public class ReviewService {
	private static final Logger LOG = LoggerFactory.getLogger(ReviewService.class);

	@Variable("searchContentsList") private String address;
	@Variable("searchContentsList") private int readTimeout;
	@Variable("searchContentsList") private int maxPoolSize;

	private ClientPool clientPool;

	@Startup
	public void init() {
		String[] target = this.address.split(":");
		this.clientPool = new ClientPool.Builder()
				.addTarget(target[0], Integer.parseInt(target[1]))
				.setMaxPoolSize(this.maxPoolSize)
				.setReadTimeout(this.readTimeout)
				.setAcquireTimeout(5000)
				.setChannelHandlerMaker(new HttpClientCodec())
				.build();
	}

	@Service
	public String searchContentsList(String isbn, String pageIndex) {
		return this.sendSearchRequest("/review", isbn, pageIndex);
	}

	@Service
	public String searchMyContentsList(String userId, String isbn, String pageIndex) {
		return this.sendSearchRequest("user/" + userId + "/review", isbn, pageIndex);
	}

	private String sendSearchRequest(String path, String isbn, String pageIndex) {
		QueryStringEncoder query = new QueryStringEncoder(path);
		if (!StringUtil.isNullOrEmpty(isbn)) {
			query.addParam("isbn", isbn);
		}

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, query.toString());
		request.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse saveResponse = client.sendAndWait(request);
			if (saveResponse.status().code() == HttpResponseStatus.OK.code()) {
				String data = "{\"isbn\":" + "\"" + isbn + "\",";
				data += "\"list\":" + saveResponse.content().toString(Charset.defaultCharset()) + "}";
				return data;
			} else {
				LOG.warn("Http status code : {}", saveResponse.status().code());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return "{\"isbn\":" + "\"" + isbn + "\", \"list\":[]}";
	}

	@Shutdown
	public void stop() {
		if (this.clientPool != null) {
			this.clientPool.destroy();
		}
	}
}
