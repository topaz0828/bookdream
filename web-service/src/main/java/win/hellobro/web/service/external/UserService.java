package win.hellobro.web.service.external;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import org.codehaus.jackson.map.ObjectMapper;
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
import win.hellobro.web.UserInfo;

import java.nio.charset.Charset;
import java.util.Map;

@ServiceDirectory(internal = true)
public class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Variable private String address;
	@Variable private int readTimeout;
	@Variable private int maxPoolSize;

	private ClientPool clientPool;
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

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
	public boolean save(UserInfo userInfo) {
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder("/user");
		queryStringEncoder.addParam("id", userInfo.getId());
		queryStringEncoder.addParam("nickname", userInfo.getNickName());
		queryStringEncoder.addParam("email", userInfo.getEmail());
		queryStringEncoder.addParam("from", userInfo.getOauthSite().val());
//		queryStringEncoder.addParam("image", userInfo.getImage());

		FullHttpRequest saveRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, queryStringEncoder.toString());
		saveRequest.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse signInResponse = client.sendAndWait(saveRequest);
			if (signInResponse.status().code() != HttpResponseStatus.CREATED.code()) {
				LOG.error("UserService ======> Fail to save user. http status code : {}", signInResponse.status().code());
				return false;
			}
		} catch (Exception e) {
			LOG.error("user service error.", e);
			return false;
		}

		return true;
	}

	@Service
	public UserInfo get(String userId) {
		int status = -1;
		HttpRequest saveRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/user/" + userId);
		saveRequest.headers().set(HttpHeaderNames.HOST, this.address);

		String body = null;
		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(saveRequest);
			status = response.status().code();

			if (status == HttpResponseStatus.OK.code()) {
				body = response.content().toString(Charset.defaultCharset());
				Map<String, Object> data = JSON_MAPPER.readValue(body, Map.class);
				UserInfo userInfo = new UserInfo();
				userInfo.setId((String) data.get("id"));
				userInfo.setNickName((String) data.get("nickName"));
				userInfo.setEmail((String) data.get("email"));
				userInfo.setImage((String) data.get("image"));
				userInfo.setOauthSite((String) data.get("oauthSite"));
				return userInfo;
			} else if (status == HttpResponseStatus.NOT_FOUND.code()) {
				return UserInfo.NOT_FOUND_USER;
			}
		} catch (Exception e) {
			LOG.error("user service error. \nbody:" + body, e);
		}

		LOG.error("UserService ======> Can not get user. Http status code : {}", status);
		return null;
	}

	@Shutdown
	public void stop() {
		if (this.clientPool != null) {
			this.clientPool.destroy();
		}
	}
}
