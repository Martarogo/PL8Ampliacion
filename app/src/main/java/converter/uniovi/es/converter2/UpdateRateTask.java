package converter.uniovi.es.converter2;


import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateRateTask extends AsyncTask<String, Void, String> {

    private final int CONVERSION_LANGUAGES = 8;
    private final String[] conversionCodes = new String[]{"USD/INR", "USD/CNY", "USD/EUR", "USD/USD", "USD/JPY", "USD/CHF", "USD/MXN", "USD/GBP"};

    @Override
    protected void onPostExecute(String result) {
        double[] conversionDouble = new double[CONVERSION_LANGUAGES];

        String[] resultArray = result.split(" ");

        try {
            for (int i=0; i<resultArray.length; i++) {
                conversionDouble[i] = Double.parseDouble(resultArray[i]);
            }
        }
        catch (NumberFormatException nfe) {
            return;
        }
        new MainActivity().SetValues(conversionDouble);
    }

    @Override
    protected String doInBackground(String... urls) {
        String stringValues = "";
        try {
            stringValues = getCurrencyRateUsdRate(urls[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return stringValues;
    }

    protected String getCurrencyRateUsdRate(String url) throws IOException {
        return parseDataFromNetwork(readStream(openUrl(url)));
    }

    private String parseDataFromNetwork(String data) {
        String conversionValues = "";

        JSONObject dataJSON = null;
        try {

            dataJSON = new JSONObject(data);
            JSONArray values = dataJSON.getJSONObject("list").getJSONArray("resources");

            for (int i=0; i<values.length(); i++) {
                for (int j=0; j<conversionCodes.length; j++) {
                    if (values.getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("name").equals(conversionCodes[j])) {
                        conversionValues = conversionValues + values.getJSONObject(i).getJSONObject("resource").getJSONObject("fields").getString("price") + " ";
                    }
                }
            }
            conversionValues = conversionValues.trim();
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        return conversionValues;

    }

    protected InputStream openUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }

    protected String readStream(InputStream urlStream) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(urlStream));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        return total.toString();
    }

}
