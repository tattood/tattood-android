package com.tattood.tattood;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import java.util.Map;

import static com.tattood.tattood.Server.error_handler;

public class InputStreamVolleyRequest extends Request<byte[]> {
    private final Response.Listener<byte[]> mListener;

    public InputStreamVolleyRequest(String mUrl, Response.Listener<byte[]> listener) {
        super(Method.GET, mUrl, error_handler);
        setShouldCache(false);
        mListener = listener;
    }

    @Override
    protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
        return null;
    }

    @Override
    protected void deliverResponse(byte[] response) {
        mListener.onResponse(response);
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }
}
