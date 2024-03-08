package com.abciloveu.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.abciloveu.configuration.properties.WebSocketProperties;

/**
 * Enable and configure Stomp over WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

	private WebSocketProperties properties;

	public WebSocketConfiguration(WebSocketProperties properties) {
		this.properties = properties;
	}

	/**
	 * Configure the message broker.
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {

		// Enable a simple memory-based message broker to send messages to the
		// client on destinations prefixed with "/queue".
		// Simple message broker handles subscription requests from clients, stores
		// them in memory, and broadcasts messages to connected clients with
		// matching destinations.

		//		registry.enableSimpleBroker("/queue");

		registry.enableSimpleBroker(properties.getTopicPrefix()); //= /topic = > /queue
		registry.setApplicationDestinationPrefixes(properties.getApplicationPrefix()); //= /app

		return;
	}

	/**
	 * Register Stomp endpoints: the url to open the WebSocket connection.
	 */
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		// Register the "/websocket" endpoint, enabling the SockJS protocol.
		// SockJS is used (both client and server side) to allow alternative
		// messaging options if WebSocket is not available.
		registry.addEndpoint(properties.getEndpoint()) ///= /gs-guide-websocket =>/websocket
				.setAllowedOrigins(properties.getAllowedOrigins())
				.withSockJS();

	}

}
