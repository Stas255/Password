package www.tolstonozhenko.password.request;

import org.json.JSONArray;

public interface VolleyJsonArrayResponseListener {
    void onError(String message);

    void onResponse(JSONArray response);
}
