package com.abciloveu.config.properties;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("app.websocket")
public class WebSocketProperties {

	/**
	 * Prefix used for WebSocket destination mappings
	 */
	private String applicationPrefix = "/app";

	/**
	 * Prefix used by topics
	 */
	private String topicPrefix = "/queue";

	/**
	 * Endpoint that can be used to connect to
	 */
	private String endpoint = "/websocket";

	/**
	 * Allowed origins
	 */
	private String[] allowedOrigins = new String[]{ "*" };

	public WebSocketProperties() {
		super();
	}

	/**
	 * The application prefix used for WebSocket destination mappings
	 * default: /app
	 * @return
	 */
	public String getApplicationPrefix() {
		return applicationPrefix;
	}

	public void setApplicationPrefix(String applicationPrefix) {
		this.applicationPrefix = applicationPrefix;
	}

	/**
	 * The Topic prefix
	 * default: /queue
	 * @return
	 */
	public String getTopicPrefix() {
		return topicPrefix;
	}

	public void setTopicPrefix(String topicPrefix) {
		this.topicPrefix = topicPrefix;
	}

	/**
	 * Get endpoint that can be used to connect to
	 * default: /websocket
	 * 
	 * @return
	 */
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String[] getAllowedOrigins() {
		return allowedOrigins;
	}

	public void setAllowedOrigins(String[] allowedOrigins) {
		this.allowedOrigins = allowedOrigins;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("applicationPrefix", applicationPrefix)
				.append("topicPrefix", topicPrefix)
				.append("endpoint", endpoint)
				.append("allowedOrigins", allowedOrigins)
				.toString();
	}

}
