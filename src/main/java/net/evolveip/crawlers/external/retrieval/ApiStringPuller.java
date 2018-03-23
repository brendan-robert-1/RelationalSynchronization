package net.evolveip.crawlers.external.retrieval;

import java.util.Base64;

import javax.net.ssl.SSLContext;

import org.apache.http.Header;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Wrapper class to provide additional utility methods to string pullers that
 * pull resources from an api in the form of a string. Extensions of this method
 * are specific implementations of external resource string pullers.
 *
 * @author brobert
 *
 */
public abstract class ApiStringPuller {
	private static final Logger logger = LoggerFactory.getLogger(ApiStringPuller.class);



	/**
	 * Utility method that returns the headers of an {@link HttpMessage} as a
	 * {@link String} for logging or debugging.
	 *
	 * @param message
	 * @return
	 */
	public String getHeadersAsString(HttpMessage message) {
		StringBuilder sb = new StringBuilder();
		sb.append(" Headers [");
		for (Header header : message.getAllHeaders()) {
			sb.append("name: " + header.getName() + " key: " + header.getValue() + " ");
		}
		sb.append("]");
		return sb.toString();
	}



	/**
	 * Utility method that returns the headers of an {@link HttpResponse} as a
	 * {@link String} for logging or debugging.
	 *
	 * @param response
	 * @return
	 */
	public String getHeadersAsString(HttpResponse response) {
		StringBuilder sb = new StringBuilder();
		for (Header header : response.getAllHeaders()) {
			sb.append(" header key: " + header.getName() + ", header value: " + header.getValue());
		}
		return sb.toString();
	}



	/**
	 * Utility method that takes a client and a request, executes the two and
	 * returns the resulting reponse as a String.
	 *
	 * @param request
	 * @param httpClient
	 * @return
	 */
	public String executeRequest(HttpRequestBase request, HttpClient httpClient) {
		String response = null;
		BasicResponseHandler responseHandler = new BasicResponseHandler();
		HttpResponse httpResp = null;
		try {
			logger.info("Executing " + request.toString());
			httpResp = httpClient.execute(request);
			response = responseHandler.handleResponse(httpResp);
			logger.info("Get request complete. Status: " + httpResp.getStatusLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}



	/**
	 * Utility method to print {@link HttpRequestBase} for debugging.
	 *
	 * @param request
	 */
	public void printFormattedRequest(HttpRequestBase request) {
		System.out.println();
		System.out.println(request.getMethod() + " " + request.getURI().toString() + "\n");
		for (Header h : request.getAllHeaders()) {
			System.out.println(h.toString());
		}
		System.out.println();
	}



	/**
	 * Utility wrapper method that takes an ugly json string and prettifies it.
	 *
	 * @param uglyJson
	 * @return
	 */
	public String getPrettyJson(String uglyJson) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(uglyJson);
		String prettyJsonString = gson.toJson(je);
		return prettyJsonString;
	}



	/**
	 * Wrapper method for base 64 encoding
	 *
	 * @param creds
	 * @return
	 */
	public String base64Encode(String creds) {
		return Base64.getEncoder().encodeToString(creds.getBytes());
	}



	/**
	 * Constructs an http client from a given sslContext.
	 *
	 * @param ssl
	 * @return
	 */
	public HttpClient constructHttpClient(SSLContext ssl) {
		SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(ssl, null, null, new NoopHostnameVerifier());
		HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory).build();
		return httpClient;
	}



	/**
	 * More verbose {@link HttpMessage} to String wrapper for debugging.
	 *
	 * @param message
	 * @return
	 */
	public String getCompleteRequest(HttpMessage message) {
		return message + getHeadersAsString(message);
	}
}
