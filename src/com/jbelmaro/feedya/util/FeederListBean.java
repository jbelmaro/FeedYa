package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FeederListBean {

    private ResponseDataBeanFeed responseData;
    private String responseDetails;
    private String responseStatus;

    public static class ResponseDataBeanFeed {

        private String query;
        private FeederBean[] entries;

        public String getQuery() {
            return query;
        }

        public void setQuery(String query) {
            this.query = query;
        }

        public FeederBean[] getEntries() {
            return entries;
        }

        public void setEntries(FeederBean[] entries) {
            this.entries = entries;
        }
    }

    public ResponseDataBeanFeed getResponseData() {
        return responseData;
    }

    public void setResponseData(ResponseDataBeanFeed responseData) {
        this.responseData = responseData;
    }

    public String getResponseDetails() {
        return responseDetails;
    }

    public void setResponseDetails(String responseDetails) {
        this.responseDetails = responseDetails;
    }

    public String getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(String responseStatus) {
        this.responseStatus = responseStatus;
    }
}
