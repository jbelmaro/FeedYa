package com.jbelmaro.feedya.util;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {

    private String id;
    private String client;
    private String wave;
    private String familyName;
    private String givenName;
    private String google;
    private String facebook;
    private String windowsLive;
    private String twitter;
    private String evernote;
    private String pocket;
    private String email;
    private String gender;
    private String picture;
    private String created;
    private boolean windowsLiveConnected;
    private boolean facebookConnected;
    private boolean evernoteConnected;
    private boolean pocketConnected;
    private boolean twitterConnected;
    private boolean wordPressConnected;
    private String locale;
    private String fullName;
    private List<PaymentProviderID> paymentProviderId;
    private List<PaymentSubscriptionID> paymentSubscriptionId;
    private String reader;
    private String twitterUserId;
    private String facebookUserId;
    private String windowsLiveId;
    private String twitterProfileImageUrl;
    private String twitterProfileBannerImageUrl;
    private String source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getWave() {
        return wave;
    }

    public void setWave(String wave) {
        this.wave = wave;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getGoogle() {
        return google;
    }

    public void setGoogle(String google) {
        this.google = google;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getWindowsLive() {
        return windowsLive;
    }

    public void setWindowsLive(String windowsLive) {
        this.windowsLive = windowsLive;
    }

    public String getEvernote() {
        return evernote;
    }

    public void setEvernote(String evernote) {
        this.evernote = evernote;
    }

    public String getPocket() {
        return pocket;
    }

    public void setPocket(String pocket) {
        this.pocket = pocket;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isWindowsLiveConnected() {
        return windowsLiveConnected;
    }

    public void setWindowsLiveConnected(boolean windowsLiveConnected) {
        this.windowsLiveConnected = windowsLiveConnected;
    }

    public boolean isFacebookConnected() {
        return facebookConnected;
    }

    public void setFacebookConnected(boolean facebookConnected) {
        this.facebookConnected = facebookConnected;
    }

    public boolean isEvernoteConnected() {
        return evernoteConnected;
    }

    public void setEvernoteConnected(boolean evernoteConnected) {
        this.evernoteConnected = evernoteConnected;
    }

    public boolean isPocketConnected() {
        return pocketConnected;
    }

    public void setPocketConnected(boolean pocketConnected) {
        this.pocketConnected = pocketConnected;
    }

    public boolean isTwitterConnected() {
        return twitterConnected;
    }

    public void setTwitterConnected(boolean twitterConnected) {
        this.twitterConnected = twitterConnected;
    }

    public boolean isWordPressConnected() {
        return wordPressConnected;
    }

    public void setWordPressConnected(boolean wordPressConnected) {
        this.wordPressConnected = wordPressConnected;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public List<PaymentProviderID> getPaymentProviderId() {
        return paymentProviderId;
    }

    public void setPaymentProviderId(List<PaymentProviderID> paymentProviderId) {
        this.paymentProviderId = paymentProviderId;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getReader() {
        return reader;
    }

    public void setReader(String reader) {
        this.reader = reader;
    }

    public String getTwitterUserId() {
        return twitterUserId;
    }

    public void setTwitterUserId(String twitterUserId) {
        this.twitterUserId = twitterUserId;
    }

    public String getFacebookUserId() {
        return facebookUserId;
    }

    public void setFacebookUserId(String facebookUserId) {
        this.facebookUserId = facebookUserId;
    }

    public String getTwitterProfileImageUrl() {
        return twitterProfileImageUrl;
    }

    public void setTwitterProfileImageUrl(String twitterProfileImageUrl) {
        this.twitterProfileImageUrl = twitterProfileImageUrl;
    }

    public String getTwitterProfileBannerImageUrl() {
        return twitterProfileBannerImageUrl;
    }

    public void setTwitterProfileBannerImageUrl(String twitterProfileBannerImageUrl) {
        this.twitterProfileBannerImageUrl = twitterProfileBannerImageUrl;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getWindowsLiveId() {
        return windowsLiveId;
    }

    public void setWindowsLiveId(String windowsLiveId) {
        this.windowsLiveId = windowsLiveId;
    }

    public List<PaymentSubscriptionID> getPaymentSubscriptionId() {
        return paymentSubscriptionId;
    }

    public void setPaymentSubscriptionId(List<PaymentSubscriptionID> paymentSubscriptionId) {
        this.paymentSubscriptionId = paymentSubscriptionId;
    }

}
