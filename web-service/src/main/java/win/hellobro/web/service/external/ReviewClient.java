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
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.balam.exof.client.Client;
import team.balam.exof.client.ClientPool;
import team.balam.exof.client.component.HttpClientCodec;
import team.balam.exof.module.service.annotation.ServiceDirectory;
import team.balam.exof.module.service.annotation.Shutdown;
import team.balam.exof.module.service.annotation.Startup;
import team.balam.exof.module.service.annotation.Variable;
import win.hellobro.web.presentation.vo.BookInfo;

import java.nio.charset.Charset;

@ServiceDirectory(internal = true)
public class ReviewClient {
	private static final Logger LOG = LoggerFactory.getLogger(ReviewClient.class);

	@Variable private String address;
	@Variable private int readTimeout;
	@Variable private int maxPoolSize;

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

	public String sendSearchRequest(String userId, String isbn, String pageIndex, String pageSize, boolean isMyContents) {
		QueryStringEncoder query = new QueryStringEncoder("/review");
		query.addParam("offset", pageIndex);
		query.addParam("limit", pageSize);
		if (!StringUtil.isNullOrEmpty(isbn)) {
			query.addParam("isbn", isbn);
		}

		if (isMyContents) {
			query.addParam("userId", userId);
		}

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, query.toString());
		request.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse saveResponse = client.sendAndWait(request);
			if (saveResponse.status().code() == HttpResponseStatus.OK.code()) {
				String data = "{\"isbn\":\"" + isbn + "\", \"id\":\"" + userId + "\",";
				data += "\"list\":" + saveResponse.content().toString(Charset.defaultCharset()) + "}";
				return data;
			} else {
				printHttpStatusCode(saveResponse);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return "{\"isbn\":" + "\"" + isbn + "\", \"list\":[]}";
	}

	public long getBookId(BookInfo bookInfo) throws Exception {
		QueryStringEncoder encoder = new QueryStringEncoder("/book-id");
		encoder.addParam("isbn", bookInfo.getIsbn());
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
		request.headers().set(HttpHeaderNames.HOST, this.address);
		boolean isSaveNewBook = false;

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);
			if (response.status().code() == HttpResponseStatus.OK.code()) {
				return Integer.parseInt(response.content().toString(Charset.defaultCharset()));
			} else if (response.status().code() == HttpResponseStatus.NOT_FOUND.code()) {
				isSaveNewBook = true;
			} else {
				printHttpStatusCode(response);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}

		if (isSaveNewBook) {
			return this.saveBookInfo(bookInfo);
		} else {
			throw new Exception("Fail to save book info.");
		}
	}

	private long saveBookInfo(BookInfo bookInfo) throws Exception {
		byte[] content = RequestConverter.convert(bookInfo);

		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/book");
		request.headers().set(HttpHeaderNames.HOST, this.address);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
		request.content().writeBytes(content);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse saveResponse = client.sendAndWait(request);
			if (saveResponse.status().code() == HttpResponseStatus.OK.code()) {
				return Integer.parseInt(saveResponse.content().toString(Charset.defaultCharset()));
			} else {
				printHttpStatusCode(saveResponse);
				throw new Exception("Fail to save book info.");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw e;
		}
	}

	public boolean saveReviewAndImpression(String userId, long bookId, String text, String type) {
		byte[] content = RequestConverter.convert(bookId, userId, text, type);

		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/review");
		request.headers().set(HttpHeaderNames.HOST, this.address);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
		request.content().writeBytes(content);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse saveResponse = client.sendAndWait(request);
			if (saveResponse.status().code() == HttpResponseStatus.OK.code()) {
				return true;
			} else {
				printHttpStatusCode(saveResponse);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return false;
	}

	public int getReviewCount(String userId) {
		return this.getCount(userId, "R"); // review
	}

	public int getImpressionCount(String userId) {
		return this.getCount(userId, "C"); // comment
	}

	private int getCount(String userId, String type) {
		QueryStringEncoder encoder = new QueryStringEncoder("/user/" + userId + "/review/count");
		encoder.addParam("type", type);

		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, encoder.toString());
		request.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);
			if (response.status().code() == HttpResponseStatus.OK.code()) {
				return Integer.parseInt(response.content().toString(Charset.defaultCharset()));
			} else {
				printHttpStatusCode(response);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return 0;
	}

	public boolean updateContents(String userId, long bookId, String type, long reviewId, String text) {
		byte[] content = RequestConverter.convert(bookId, userId, text, type);

		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.PUT, "/review/" + reviewId);
		request.headers().set(HttpHeaderNames.HOST, this.address);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, content.length);
		request.content().writeBytes(content);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);

			if (response.status().code() == HttpResponseStatus.OK.code()) {
				return true;
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}


		return false;
	}

	public String getReviewOrImpression(String reviewId) {
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/review/" + reviewId);
		request.headers().set(HttpHeaderNames.HOST, this.address);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);
			if (response.status().code() == HttpResponseStatus.OK.code()) {
				return response.content().toString(Charset.defaultCharset());
			} else {
				LOG.error("fail to get review. http status code: {}", response.status().code());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}

		return "{}";
	}

	public void deleteContents(String contentsId, String userId) {
		byte[] body = RequestConverter.convert(contentsId, userId);

		FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.DELETE, "/review/" + contentsId);
		request.headers().set(HttpHeaderNames.HOST, this.address);
		request.headers().set(HttpHeaderNames.CONTENT_LENGTH, body.length);
		request.content().writeBytes(body);

		try (Client client = this.clientPool.get()) {
			FullHttpResponse response = client.sendAndWait(request);
			if (response.status().code() != HttpResponseStatus.OK.code()) {
				LOG.error("### fail to remove review. http status code: {}", response.status().code());
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Shutdown
	public void stop() {
		if (this.clientPool != null) {
			this.clientPool.destroy();
		}
	}

	private static void printHttpStatusCode(FullHttpResponse response) {
		LOG.warn("Http status code : {}", response.status().code());
	}
}
