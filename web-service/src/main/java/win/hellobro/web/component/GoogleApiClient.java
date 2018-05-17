package win.hellobro.web.component;

import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.codehaus.jackson.map.ObjectMapper;
import team.balam.exof.client.Client;
import team.balam.exof.client.DefaultClient;
import win.hellobro.web.component.part.HttpsClientCodec;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Map;

public class GoogleApiClient {
	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private GoogleApiClient() {
	}

	@SuppressWarnings("unchecked")
	public static OAuthAccessToken getAccessToke(String url, String clientId, String redirectUri, String clientSecret, String code) throws Exception {
		URI targetUri = new URI(url);
		StringBuilder param = new StringBuilder();
		param.append("client_id=").append(clientId)
				.append("&redirect_uri=").append(redirectUri)
				.append("&code=").append(code)
				.append("&client_secret=").append(clientSecret)
				.append("&grant_type=authorization_code");
		byte[] body = param.toString().getBytes();

		FullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, targetUri.getPath());
		httpRequest.headers().set(HttpHeaderNames.HOST, targetUri.getHost());
		httpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length);
		httpRequest.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/x-www-form-urlencoded");
		httpRequest.content().writeBytes(body);

		FullHttpResponse response;
		try (Client client = new DefaultClient(new HttpsClientCodec())) {
			client.setConnectTimeout(10000);
			client.connect(targetUri.getHost(), 443);
			response = client.sendAndWait(httpRequest);
		}

		String resBody = response.content().toString(Charset.forName("UTF-8"));
		return new OAuthAccessToken(JSON_MAPPER.readValue(resBody, Map.class));
	}
}
