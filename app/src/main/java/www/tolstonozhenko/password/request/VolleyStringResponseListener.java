package www.tolstonozhenko.password.request;

public interface VolleyStringResponseListener {
    void onError(String message);

    void onResponse(String response);
}