package www.tolstonozhenko.password.request;

import org.json.JSONObject;

public interface VolleyJsonResponseListener {
    void onError(String message);

    void onResponse(JSONObject response);
}

