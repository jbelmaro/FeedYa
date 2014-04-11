package com.jbelmaro.feedya.util;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Visual
{
    private String url;
    private String height;
    private String width;
    private String contentType;
    private Long expirationDate;
    private String processor;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
    public Long getExpirationDate() {
        return expirationDate;
    }
    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }
    public String getProcessor() {
        return processor;
    }
    public void setProcessor(String processor) {
        this.processor = processor;
    }
    
}