package com.example.armen.pl.io.rest;


import com.example.armen.pl.util.Constant;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.armen.pl.util.Constant.Util.UTF_8;

public class HttpRequestManager {

    public static HttpURLConnection executeRequest(String apiUrl, String requestMethod, String data) {
        HttpURLConnection connection = null;

        try {
            URL ulr = new URL(apiUrl);
            connection = (HttpURLConnection) ulr.openConnection();
            connection.setRequestMethod(requestMethod);
            connection.setUseCaches(false);

            switch (requestMethod) {
                case Constant.RequestMethod.GET:
                    connection.connect();
                    break;

                case Constant.RequestMethod.PUT:
                case Constant.RequestMethod.POST:
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.connect();
                    OutputStream outputStream = connection.getOutputStream();
                    outputStream.write(data != null ? data.getBytes(UTF_8) : new byte[]{0});
                    outputStream.flush();
                    outputStream.close();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
