package win.hellobro.web.component;

import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringEncoder;
import org.codehaus.jackson.map.ObjectMapper;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import win.hellobro.web.component.part.HttpsClientCodec;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

public class FbApiClient {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private FbApiClient() {

	}

	@SuppressWarnings("unchecked")
	public static FbAccessToken getAccessToken(String uri, String clientId, String redirectUri, String clientSecret, String code) throws Exception {
		URI targetUri = new URI(uri);
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder(targetUri.getPath());
		queryStringEncoder.addParam("client_id", clientId);
		queryStringEncoder.addParam("redirect_uri", redirectUri);
		queryStringEncoder.addParam("client_secret", clientSecret);
		queryStringEncoder.addParam("code", code);

		FullHttpResponse response;

		try (Client client = new DefaultClient(new HttpsClientCodec())) {
			HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, queryStringEncoder.toString());
			httpRequest.headers().set(HttpHeaderNames.HOST, targetUri.getHost());

			client.connect(targetUri.getHost(), 443);
			response = client.sendAndWait(httpRequest);
		}

		String body = response.content().toString(Charset.forName("UTF-8"));
		return new FbAccessToken(JSON_MAPPER.readValue(body, Map.class));
	}

	@SuppressWarnings("unchecked")
	public static FbUserInfo getUserInfo(String uri, String accessToken) throws Exception {
		URI targetUri = new URI(uri);
		QueryStringEncoder queryStringEncoder = new QueryStringEncoder(targetUri.getPath());
		queryStringEncoder.addParam("access_token", accessToken);
		queryStringEncoder.addParam("fields", "id,name,picture,email");

		FullHttpResponse response;

		try (Client client = new DefaultClient(new HttpsClientCodec())) {
			HttpRequest httpRequest = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, queryStringEncoder.toString());
			httpRequest.headers().set(HttpHeaderNames.HOST, targetUri.getHost());

			client.connect(targetUri.getHost(), 443);
			response = client.sendAndWait(httpRequest);
		}

		String body = response.content().toString(Charset.forName("UTF-8"));
		return new FbUserInfo(JSON_MAPPER.readValue(body, Map.class));
	}
}
