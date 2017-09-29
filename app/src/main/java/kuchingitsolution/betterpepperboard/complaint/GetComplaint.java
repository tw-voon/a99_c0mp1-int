package kuchingitsolution.betterpepperboard.complaint;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.DB_Offline;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.network.RequestBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetComplaint {

    private Context context;
    private Session session;
    private DB_Offline db_offline;

    public GetComplaint(Context context){
        this.context = context;
        session = new Session(context);
        db_offline = new DB_Offline(context);
    }

    public void pull_complaint(){
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... url) {
                Response response = null;
                OkHttpClient client = new OkHttpClient();
                RequestBody body = RequestBuilder.getComplaint(session.getUserID());
                Request request = new Request.Builder()
                        .url(url[0])
                        .post(body)
                        .build();
                try {
                    response = client.newCall(request).execute();
                    return response.body().string();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e("ANSWER", "" + result);
                if(result == null || result.equals("null"))
                    Toast.makeText(context, "NULL", Toast.LENGTH_SHORT).show();
                else
                    process_response(result);
//                    db_offline.insertReport(result);
            }
        }.execute(Config.GET_REPORT);
    }

    private void process_response(String result){

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("report");
            JSONArray response = jsonObject.getJSONArray("response");
            int length = jsonArray.length();

            for (int i = 0; i < length; i++){
                JSONObject data = jsonArray.getJSONObject(i);
                db_offline.insertComplaint(data);
            }
            db_offline.insertResponse(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        broadcast();
    }

    private void broadcast(){
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("reload", "true");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        Log.d("sender", "Broadcasting message");
    }
}
