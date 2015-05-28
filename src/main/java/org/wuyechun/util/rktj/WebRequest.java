package org.wuyechun.util.rktj;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class WebRequest {
	private static final Logger LOG = Logger.getLogger(WebRequest.class);
	private static String LINE_SEPERATOR = System.getProperty("line.separator", "\n");
	private Map<String, String> cookies = new HashMap<String, String>();
	
	public String get(String url, Map<String, String> headers) throws MalformedURLException, IOException {
		return fetch(url, StringUtils.EMPTY, "GET", headers == null ? new HashMap<String, String>() : headers);
	}
	
	public String post(String url, String data, Map<String, String> headers) throws MalformedURLException, IOException {
		return fetch(url, data, "POST", headers == null ? new HashMap<String, String>() : headers);
	}

	@SuppressWarnings("unchecked")
	private String fetch(String url, String postData, String method, 
			Map<String, String> headers) throws MalformedURLException, IOException {
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("Invoke http request:" + LINE_SEPERATOR + "Url: " + url + LINE_SEPERATOR + "Post data: " + postData);
		}
		
		StringBuilder output = new StringBuilder("");
		OutputStream outputStream = null;
		InputStream inputStream = null;
		
		try {
			URL urlObject = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)urlObject.openConnection();
			conn.setRequestMethod(method);
			conn.setDoOutput(true);
			conn.setDoInput(true);
			
			appendHeaders(conn, headers);
			appendCookies(conn, cookies);
			
			if (!StringUtils.isEmpty(postData)) {
				conn.addRequestProperty("Content-Length", Integer.toString(postData.getBytes().length));
			}
			
			if (StringUtils.equalsIgnoreCase(method, "post")) {
				outputStream = conn.getOutputStream();
				String charset = getCharsetFromHeaders(headers);
				IOUtils.write(postData, outputStream, charset);
			}
			
			Map<String, String> responseHeaders = getHeaders(conn);
			cookies = getCookies(responseHeaders, cookies);
			
			String charset = getCharsetFromHeaders(responseHeaders);
			inputStream = conn.getInputStream();
			List<String> lines = IOUtils.readLines(inputStream, charset);
			
			headers.clear();
			headers.putAll(responseHeaders);
			
			for (String line : lines) {
				output.append(line).append(LINE_SEPERATOR);
			}
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Receive response from server: " + LINE_SEPERATOR + output);
			}
			
			return output.toString();
		}
		finally {
			try { inputStream.close(); } catch (Exception e) { }
			try { outputStream.close(); } catch (Exception e) { }
		}
	}
	
	private Map<String, String> getHeaders(URLConnection connection) {
		Map<String, String> headers = new HashMap<String, String>();
		String key;
		int i = 1;
		while ((key = connection.getHeaderFieldKey(i)) != null) {
			String value = connection.getHeaderField(i); 
			headers.put(key, value);
			
			i++;
		}
		return headers;
	}

	private Map<String, String> getCookies(Map<String, String> headers, Map<String, String> cookies) {
		String key = "Set-Cookie";
		String header = headers.get(key);
		if (StringUtils.isEmpty(header)) {
			header = "";
		}
		StringTokenizer st = new StringTokenizer(header, ",");
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
			String cookie = s.substring(0, s.indexOf(";"));
			int j = cookie.indexOf("=");
			if (j != -1) {
				String cookieName = cookie.substring(0, j);
				String cookieValue = cookie.substring(j + 1);
				cookies.remove(cookieName);
				cookies.put(cookieName, cookieValue);
			}
		}
		return cookies;
	}

	private void appendHeaders(URLConnection connection, Map<String, String> headers) {
		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				connection.setRequestProperty(header.getKey(), header.getValue());
			}
		}
	}

	private void appendCookies(URLConnection connection, Map<String, String> cookies) {
		int i = 0;
		String s = "";
		for (Entry<String, String> entry : cookies.entrySet()) {
			s += entry.getKey() + "=" + entry.getValue();
			if (i < cookies.size() - 1) {
				s += ";";
			}
			i++;
		}
		
		if (!s.equals("")) {
			connection.setRequestProperty("Cookie", s);
		}
	}

	private String getCharsetFromHeaders(Map<String, String> headers) {
		String charset = "utf-8";
		
		if (!headers.containsKey("Content-Type")) {
			return charset;
		}
		
		String contentType = headers.get("Content-Type");
		
		String[] parts = StringUtils.split(contentType, ";");
		for (String part : parts) {
			String[] keyValue = StringUtils.split(part, "=");
			if (keyValue.length > 1) {
				String key = StringUtils.trim(keyValue[0]);
				String value = StringUtils.trim(keyValue[1]);
				if (StringUtils.equals(key, "charset")) {
					charset = value;
					break;
				}
			}
		}
	
		return charset;
	}
}
