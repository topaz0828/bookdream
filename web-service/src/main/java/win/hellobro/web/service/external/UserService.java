package win.hellobro.web.service.external;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
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
import win.hellobro.web.OAuthSite;
import win.hellobro.web.UserInfo;

import java.nio.charset.Charset;
import java.util.HashMap;
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
	public void checkEmailAndNickname(String email, String nickname) throws DuplicateException {
		QueryStringEncoder encoder = new QueryStringEncoder("/check");
		encoder.addParam("email", email);
		encoder.addParam("nickname", nickname);

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
		request.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);
			if (response.status().code() == HttpResponseStatus.CONFLICT.code()) {
				String body = response.content().toString(Charset.defaultCharset());
				if (body.contains("eMail")) {
					throw new DuplicateException("email");
				} else if (body.contains("NickName")) {
					throw new DuplicateException("nickname");
				}
			}
		} catch (DuplicateException e){
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("UserService error.");
		}
	}

	@Service
	public boolean save(UserInfo userInfo) {
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder("/user");
		queryStringEncoder.addParam("id", userInfo.getId());
		queryStringEncoder.addParam("nickname", userInfo.getNickName());
		queryStringEncoder.addParam("email", userInfo.getEmail());
		queryStringEncoder.addParam("from", userInfo.getOauthSite().val());
		queryStringEncoder.addParam("image", userInfo.getImage());
		queryStringEncoder.addParam("oauthId", userInfo.getOauthId());

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
	@SuppressWarnings("unchecked")
	public UserInfo get(String oauthId, OAuthSite site) {
		QueryStringEncoder encoder = new QueryStringEncoder("/user/oauth");
		encoder.addParam("oauthId", oauthId);
		encoder.addParam("from", site.val());

		int status = -1;
		HttpRequest saveRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
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
				userInfo.setOauthId((String) data.get("oauthId"));
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

	@Service
	public void updateProfileImage(String email, OAuthSite from, String profileUrl) throws Exception {
		QueryStringEncoder encoder = new QueryStringEncoder("/user");
		encoder.addParam("email", email);
		encoder.addParam("from", from.val());

		HashMap<String, String> user = new HashMap<>();
		user.put("image", profileUrl);
		byte[] body = JSON_MAPPER.writeValueAsBytes(user);

		FullHttpRequest saveRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PATCH, encoder.toString());
		saveRequest.headers().set(HttpHeaderNames.HOST, this.address);
		saveRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
		saveRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length);
		saveRequest.content().writeBytes(body);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(saveRequest);

			if (response.status().code() != HttpResponseStatus.OK.code()) {
				LOG.error("=====> UserService Fail to update profile image. status:{}", response.status().code());
			}
		} catch (Exception e) {
			LOG.error("Fail to update profile image.", e);
		}
	}

	@Shutdown
	public void stop() {
		if (this.clientPool != null) {
			this.clientPool.destroy();
		}
	}
}
