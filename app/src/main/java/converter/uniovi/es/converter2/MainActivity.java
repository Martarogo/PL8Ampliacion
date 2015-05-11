package converter.uniovi.es.converter2;

import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends ActionBarActivity {

    private final String UPDATE_URL = "http://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
    private final static int CONVERSION_LANGUAGES = 8;
    private HashMap<Integer, EditText> _map;

    private static double[] conversionToDollars;
    private EditText[] editTextArray;
    private int[] IDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conversionToDollars = new double[CONVERSION_LANGUAGES];
        editTextArray = new EditText[CONVERSION_LANGUAGES];
        IDs = new int[]{R.id.editTextRupias, R.id.editTextYuans, R.id.editTextEuros, R.id.editTextDollars, R.id.editTextYens, R.id.editTextFrancs, R.id.editTextPesos, R.id.editTextPounds};
        _map = new HashMap<Integer, EditText>(CONVERSION_LANGUAGES);


        for (int i=0; i<editTextArray.length; i++) {
            editTextArray[i] = (EditText) findViewById(IDs[i]);
            _map.put(new Integer(i), editTextArray[i]);
            _map.get(i).setOnFocusChangeListener(onFocusChangeListener);
        }

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        new UpdateRateTask().execute(UPDATE_URL);
                    }
                });
            }
        };
        timer.schedule(timerTask, 0, 60000);
    }

    View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                CleanValues();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetValues(double[] values) {
        for (int i=0; i<values.length; i++) {
            conversionToDollars[i] = values[i];
        }
    }

    public void CleanValues() {
        for (int i=0; i<_map.size(); i++) {
            _map.get(i).setText("");
        }
    }

    public void ChooseValues(View v) {
        EditText editTextSource = null;

        for (int i=0; i<_map.size(); i++) {
            if (!_map.get(i).getText().toString().equals("")) {
                editTextSource = _map.get(i);
            }
        }

        if (editTextSource != null) {
            int toDollarsPosition = Integer.parseInt(editTextSource.getTag().toString());

            int toDestinationCoinPosition = Integer.parseInt(v.getTag().toString());

            EditText editTextDestination = _map.get(toDestinationCoinPosition);

            double toDollarsFactor = 1 / conversionToDollars[toDollarsPosition];
            double toDestinationCoinFactor = conversionToDollars[toDestinationCoinPosition];

            convert(editTextSource, editTextDestination, toDollarsFactor, toDestinationCoinFactor);
        }
        else {
            Toast.makeText(getApplicationContext(), "Introduce un valor en algún cuadro de texto", Toast.LENGTH_LONG).show();
        }
    }

    public void convert(EditText editTextSource, EditText editTextDestination, double toDollarsFactor, double toDestinationCoinFactor) {
        String StringSource = editTextSource.getText().toString();

        double NumberSource;
        try {
            NumberSource = Double.parseDouble(StringSource);
        }
        catch (NumberFormatException nfe) {
            return;
        }

        double numberInDollars = NumberSource * toDollarsFactor;

        double NumberDestination = numberInDollars * toDestinationCoinFactor;

        String StringDestination = Double.toString(NumberDestination);

        editTextDestination.setText(StringDestination);
    }
}
