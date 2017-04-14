package com.moyo.carzrideon.Volley;

/**
 * Created by Nikil on 11/12/2016.
 */
/**
 * Created by nikhi on 7/25/2016.
 */
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.moyo.carzrideon.Constants.Constants;


public class VolleyJsonRequest extends Request<JSONObject> {

    private Listener<JSONObject> listener;
    private Map<String, String> params;
    private Map<String, String> headers;

    public VolleyJsonRequest(String url, Map<String, String> params,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(Method.GET, Constants.BASE_URL+url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public VolleyJsonRequest(int method, String url, Map<String, String> params,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, Constants.BASE_URL+url, errorListener);
        this.listener = reponseListener;
        this.params = params;
    }

    public VolleyJsonRequest(int method, String url, Map<String, String> params,Map<String,String> headers,
                             Listener<JSONObject> reponseListener, ErrorListener errorListener) {
        super(method, Constants.BASE_URL+url, errorListener);

        this.listener = reponseListener;
        this.params = params;
        this.headers=headers;
    }

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return params;
    }

    public Map<String, String> getHeaders() throws AuthFailureError {

       return headers;
    }
    ;

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }



    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }
}