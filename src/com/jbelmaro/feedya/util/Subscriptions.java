package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Subscriptions {

	private Subscription[] subscriptions;

	public Subscription[] getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(Subscription[] subscriptions) {
		this.subscriptions = subscriptions;
	}

}
