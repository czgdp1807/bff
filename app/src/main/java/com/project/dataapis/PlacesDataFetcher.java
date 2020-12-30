package com.project.dataapis;

import android.os.AsyncTask;
import android.util.Pair;

import com.fasterxml.jackson.databind.JsonNode;
import com.project.config.GoogleAPIKeys;
import com.project.config.GoogleURLs;

import java.io.IOException;
import java.util.HashMap;

public class PlacesDataFetcher extends AsyncTask<HashMap<String, String>, Boolean, Integer> implements DataFetcherInterface
{

    private String APIKey;
    private JsonNode data;
    private Integer responseCode;
    private String IOExceptioError;

    public PlacesDataFetcher()
    {
        APIKey = "";
        data = null;
        responseCode = 404;
        IOExceptioError = "";
    }

    @Override
    public void setAPIKey()
    {
        APIKey = GoogleAPIKeys.Places;
    }

    private void fetchData(HashMap<String, String> queryMap)
    {
        try
        {
            Pair<JsonNode, Integer> results = Util.fetchData(queryMap, APIKey, GoogleURLs.Places);
            data = results.first;
            responseCode = results.second;
        }
        catch (IOException e)
        {
            IOExceptioError = e.toString();
        }
    }

    @Override
    protected Integer doInBackground(HashMap<String, String>... hashMaps)
    {
        fetchData(hashMaps[0]);
        return responseCode;
    }

    @Override
    public HashMap<String, String> toHashMap()
    {
        return null;
    }
}
