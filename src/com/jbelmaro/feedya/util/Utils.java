package com.jbelmaro.feedya.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jbelmaro.feedya.R;

public class Utils {

    /**
     * Parse request object to JSON.
     * 
     * @param request
     * @return JSON String
     */
    public static String parseRequest2JSON(Object request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ByteArrayOutputStream baosRequest = new ByteArrayOutputStream();
        try {
            mapper.writeValue(baosRequest, request);
            return baosRequest.toString();
        } catch (Exception e) {
            throw new Exception("Wrong request " + request + ".Error: " + e.getMessage());
        } finally {
            if (baosRequest != null) {
                try {
                    baosRequest.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static StreamContentResponse parseJSONToArticleListBean(String value) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        StreamContentResponse bean = null;
        try {
            bean = mapper.readValue(value, StreamContentResponse.class);
        } catch (JsonGenerationException e) {

            e.printStackTrace();

        } catch (JsonMappingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return bean;
    }

    public static ArticleListBean parseJSONToArticleListBean2(String value) throws Exception {
        JsonFactory f = new JsonFactory();
        JsonParser jp = f.createParser(value);
        jp.nextToken(); // will return JsonToken.START_OBJECT (verify?)
        while (jp.nextToken() != JsonToken.END_OBJECT) {
        }
        jp.getCurrentName();
        ArticleListBean bean = null;

        return bean;
    }

    public static SearchFeedsResponse parseJSONToFeederListBean(String value) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        SearchFeedsResponse bean = null;
        try {
            bean = mapper.readValue(value, SearchFeedsResponse.class);
        } catch (JsonGenerationException e) {

            e.printStackTrace();

        } catch (JsonMappingException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return bean;
    }

    public static StreamContentResponse LoadFeeds(String query, String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/" + URLEncoder.encode(query, "UTF-8") + "/contents?count=10");
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static StreamContentResponse LoadMoreFeeds(String query, String authCode, Resources resources,
            String continuation) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/" + URLEncoder.encode(query, "UTF-8") + "/contents?count=10&continuation="
                    + continuation);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static StreamContentResponse LoadLatest(String user, String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/contents?streamId=user%2F" + URLEncoder.encode(user, "UTF-8")
                    + "%2Fcategory%2Fglobal.all&count=10");
            System.out.println("URL: " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static StreamContentResponse LoadCategory(String authCode, String categoryId,
            Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/contents?streamId=" + URLEncoder.encode(categoryId, "UTF-8") + "%2F"
                    + "&count=10");
            System.out.println("URL: " + URLEncoder.encode(categoryId, "UTF-8"));

            System.out.println("URL: " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static StreamContentResponse LoadCategoryMore(String authCode, String categoryId,
            Resources resources, String continuation) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/contents?streamId=" + URLEncoder.encode(categoryId, "UTF-8") + "%2F"
                    + "&count=10&continuation="
                    + continuation);
            System.out.println("URL: " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static StreamContentResponse LoadSavedForLater(String user, String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        StreamContentResponse listA = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String feedURL = props.getProperty("feedPath");

        try {
            url = new URL(feedURL + "/contents?streamId=user%2F" + URLEncoder.encode(user, "UTF-8")
                    + "%2Ftag%2Fglobal.saved");
            System.out.println("URL: " + url);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listA = Utils.parseJSONToArticleListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return listA;
    }

    public static SearchFeedsResponse FindFeeds(String query, String authCode, Resources resources) {
        URL url;
        HttpURLConnection connection;
        String line;
        StringBuilder builder;
        BufferedReader reader = null;
        SearchFeedsResponse listF = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String searchURL = props.getProperty("searchPath");
        try {
            url = new URL(searchURL + "?q=" + query + "&n=20&organic=true&promoted=true");
            Log.v("SearchActivity", url.toString());
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            listF = Utils.parseJSONToFeederListBean(builder.toString());
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        return listF;
    }

    public static Bitmap downloadBitmap(String url) {
        Bitmap image = null;
        // initilize the default HTTP client object
        InputStream response = null;
        try {
            URL urldir = new URL("http://s2.googleusercontent.com/s2/favicons?domain=" + url);

            URLConnection connection = urldir.openConnection();
            connection.setUseCaches(true);

            response = (InputStream) connection.getContent();
            if (response instanceof InputStream) {

                image = BitmapFactory.decodeStream(response);

            }

        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return image;
    }

    public static Bitmap downloadArticleImage(String imageUrl) {
        InputStream input = null;
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            connection.disconnect();
            return myBitmap;
        } catch (NetworkOnMainThreadException e) {
            e.printStackTrace();
            return null;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                input.close();

            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
    }

    public static String getMetaTag(Document document, String attr) {
        Elements elements = document.select("meta[name=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null)
                return s;
        }
        elements = document.select("meta[property=" + attr + "]");
        for (Element element : elements) {
            final String s = element.attr("content");
            if (s != null)
                return s;
        }
        return null;
    }

    public static ExchangeCodeResponse getAuthToken(String code, Resources resources) {

        Properties props = new Properties();
        ExchangeCodeResponse bean = null;
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String authURL = props.getProperty("authPath");
        authURL += "?";
        authURL += code;
        authURL += "&";
        authURL += "client_id=";
        authURL += props.getProperty("clientID");
        authURL += "&";
        authURL += "client_secret=";
        authURL += props.getProperty("clientPASS");
        authURL += props.getProperty("restAuthPath");
        System.out.println("URL: " + authURL);
        HttpPost post = new HttpPost(authURL);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();

            String responseText = EntityUtils.toString(entity);
            ObjectMapper mapper = new ObjectMapper();

            try {
                bean = mapper.readValue(responseText, ExchangeCodeResponse.class);
            } catch (JsonGenerationException e) {

                e.printStackTrace();

            } catch (JsonMappingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        }
        return bean;
    }

    public static ExchangeCodeResponse getAuthTokenWithRefreshToken(String code, Resources resources,
            String refreshOrLogout) {

        Properties props = new Properties();
        ExchangeCodeResponse bean = null;
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String authURL = props.getProperty("authPath");
        authURL += "?refresh_token=";
        authURL += code;
        authURL += "&";
        authURL += "client_id=";
        authURL += props.getProperty("clientID");
        authURL += "&";
        authURL += "client_secret=";
        authURL += props.getProperty("clientPASS");
        authURL += "&grant_type=";
        authURL += refreshOrLogout;
        System.out.println("URL: " + authURL);
        HttpPost post = new HttpPost(authURL);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        try {
            response = client.execute(post);
            HttpEntity entity = response.getEntity();

            String responseText = EntityUtils.toString(entity);
            ObjectMapper mapper = new ObjectMapper();

            try {
                bean = mapper.readValue(responseText, ExchangeCodeResponse.class);
            } catch (JsonGenerationException e) {

                e.printStackTrace();

            } catch (JsonMappingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        }
        return bean;
    }

    public static Profile getProfile(String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        Profile profile = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String profileURL = props.getProperty("profilePath");

        try {
            url = new URL(profileURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                profile = mapper.readValue(builder.toString(), Profile.class);
            } catch (JsonGenerationException e) {

                e.printStackTrace();

            } catch (JsonMappingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();

            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return profile;

    }

    public static List<Category> getCategories(String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        List<Category> categories = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String categoriesURL = props.getProperty("categoriesPath");

        try {
            url = new URL(categoriesURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                categories = mapper.readValue(builder.toString(), new TypeReference<List<Category>>() {
                });
            } catch (JsonGenerationException e) {

                e.printStackTrace();

            } catch (JsonMappingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return categories;

    }

    public static List<Subscription> getSubscriptions(String authCode, Resources resources) {
        URL url = null;
        HttpURLConnection connection = null;
        String line;
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        List<Subscription> subscriptions = null;

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String subscriptionsURL = props.getProperty("subscriptionsPath");

        try {
            url = new URL(subscriptionsURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000 /* milliseconds */);
            connection.setConnectTimeout(15000 /* milliseconds */);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.setRequestProperty("Authorization", authCode);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            ObjectMapper mapper = new ObjectMapper();
            try {
                subscriptions = mapper.readValue(builder.toString(), new TypeReference<List<Subscription>>() {
                });
            } catch (JsonGenerationException e) {

                e.printStackTrace();

            } catch (JsonMappingException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            }
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        } catch (Exception e) {
            //
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }

        return subscriptions;

    }

    public static HttpResponse addSubscription(String authCode, Resources resources, Subscription subs) {

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String subscriptionsURL = props.getProperty("subscriptionsPath");
        HttpPost post = new HttpPost(subscriptionsURL);
        post.addHeader("Authorization", authCode);
        try {
            Log.v("Añadir subscripcion", mapper.writeValueAsString(subs));
            post.setEntity(new StringEntity(mapper.writeValueAsString(subs), "UTF8"));
        } catch (UnsupportedEncodingException e1) {
            //
            e1.printStackTrace();
        } catch (JsonProcessingException e1) {
            //
            e1.printStackTrace();
        }
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(post);

        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse deleteSubscription(String authCode, Resources resources, String subs) {

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String subscriptionsURL = props.getProperty("subscriptionsPath");
        HttpDelete delete = null;
        try {
            delete = new HttpDelete(subscriptionsURL + "/" + URLEncoder.encode(subs, "UTF-8"));
        } catch (UnsupportedEncodingException e1) {
            //
            e1.printStackTrace();
        }
        delete.addHeader("Authorization", authCode);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(delete);

        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();

        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse saveForLater(String authCode, String user, Resources resources, SaveForLaterItem item) {

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        String tagsURL = props.getProperty("tagsPath");
        URL url;
        HttpResponse response = null;
        try {
            url = new URL(tagsURL + "/user%2F" + URLEncoder.encode(user, "UTF-8") + "%2Ftag%2Fglobal.saved?");
            Log.v("URL SAVE FOR LATER", url.toString());

            HttpClient client = new DefaultHttpClient();
            HttpPut put = new HttpPut();
            put.setURI(url.toURI());

            put.addHeader("Authorization", authCode);

            Log.v("Añadir subscripcion", mapper.writeValueAsString(item));
            put.setEntity(new StringEntity(mapper.writeValueAsString(item), "UTF8"));

            response = client.execute(put);
        } catch (UnsupportedEncodingException e1) {
            //
            e1.printStackTrace();
        } catch (JsonProcessingException e1) {
            //
            e1.printStackTrace();
        } catch (MalformedURLException e) {
            //
            e.printStackTrace();
        } catch (URISyntaxException e) {
            //
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();
        } catch (NetworkOnMainThreadException e) {
            //
            e.printStackTrace();
        }
        return response;
    }

    public static HttpResponse deleteFromSaveLater(String authCode, Resources resources, String user, String idItem) {

        Properties props = new Properties();
        InputStream rawResource = null;
        try {
            rawResource = resources.openRawResource(R.raw.feedya);

            props.load(rawResource);
            System.out.println("The properties are now loaded");
            System.out.println("properties: " + props);
        } catch (NotFoundException e) {
            System.err.println("Did not find raw resource: " + e);
        } catch (IOException e) {
            System.err.println("Failed to open microlog property file");
        } finally {
            try {
                rawResource.close();
            } catch (IOException e) {
                //
                e.printStackTrace();
            }
        }
        String tagsURL = props.getProperty("tagsPath");
        HttpDelete delete = null;
        try {
            delete = new HttpDelete(tagsURL + "/user%2F" + URLEncoder.encode(user, "UTF-8") + "%2Ftag%2Fglobal.saved/"
                    + URLEncoder.encode(idItem, "UTF-8"));
            try {
                Log.v("BORRAR", delete.getURI().toURL().toString());
            } catch (MalformedURLException e) {
                //
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e1) {
            //
            e1.printStackTrace();
        }
        delete.addHeader("Authorization", authCode);

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(delete);

        } catch (ClientProtocolException e) {
            //
            e.printStackTrace();
        } catch (IOException e) {
            //
            e.printStackTrace();

        } catch (NetworkOnMainThreadException e) {

            e.printStackTrace();
        }
        return response;
    }

    public static String capitalize(String source) {
        boolean cap = true;
        char[] out = source.toCharArray();
        int i, len = source.length();
        for (i = 0; i < len; i++) {
            if (Character.isWhitespace(out[i])) {
                cap = true;
                continue;
            }
            if (cap) {
                out[i] = Character.toUpperCase(out[i]);
                cap = false;
            }
        }
        return new String(out);
    }
}
