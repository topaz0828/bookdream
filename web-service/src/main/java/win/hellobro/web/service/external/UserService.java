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

@ServiceDirectory
public class UserService {
	private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

	@Variable("save") private String address;
	@Variable("save") private int readTimeout;
	@Variable("save") private int maxPoolSize;

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
	public void save(UserInfo userInfo) throws UserServiceException {
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder("/user");
		queryStringEncoder.addParam("id", userInfo.getId());
		queryStringEncoder.addParam("nickname", userInfo.getNickname());
		queryStringEncoder.addParam("email", userInfo.getEmail());
		queryStringEncoder.addParam("from", userInfo.getOauthSite().val());
		queryStringEncoder.addParam("image", userInfo.getImage());

		FullHttpRequest saveRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, queryStringEncoder.toString());
		saveRequest.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse signInResponse = client.sendAndWait(saveRequest);
			if (signInResponse.status().code() != HttpResponseStatus.CREATED.code()) {
				throw new UserServiceException("Fail to save user. http status code : " + signInResponse.status().code());
			}
		} catch (Exception e) {
			throw new UserServiceException("UserService error.", e);
		}
	}

	@Service
	public UserInfo get(String userId) throws UserServiceException {
		int status;
		HttpRequest saveRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/user/" + userId);
		saveRequest.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(saveRequest);
			status = response.status().code();

			if (status == HttpResponseStatus.OK.code()) {
				//todo 응답으로 온 데이터를 세팅해 준다.
				return new UserInfo();
			} else if (status == HttpResponseStatus.NOT_FOUND.code()) {
				return null;
			}
		} catch (Exception e) {
			throw new UserServiceException("UserService error.", e);
		}

		throw new UserServiceException("Can not get user. Http status code : " + status);
	}

	@Shutdown
	public void stop() {
		if (this.clientPool != null) {
			this.clientPool.destroy();
		}
	}
}
