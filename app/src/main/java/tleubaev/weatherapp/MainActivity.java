package tleubaev.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import tleubaev.weatherapp.R;

public class MainActivity extends AppCompatActivity {

    private static final String SITE_URL = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=53e946410589d6b2da24efa2971251d9&lang=ru&units=metric";

    private EditText editText;
    private TextView textView;
    private ConstraintLayout mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_text);
        textView = findViewById(R.id.text_view);
        mainMenu = findViewById(R.id.mainMenu);
    }

    public void onClickButtonWeather(View view) {
        String cityName = editText.getText().toString().trim();
        if (!cityName.isEmpty()) {
            DownloadApiWeather api = new DownloadApiWeather();
            String url = String.format(SITE_URL, cityName);
            try {
                api.execute(url).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hideKeyboardFrom(this, view);
        } else {
            Toast.makeText(this, "Введите название города!", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadApiWeather extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    result.append(line);
                    line = reader.readLine();
                }
                return result.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                String name = jsonObject.getString("name");
                String temp = jsonObject.getJSONObject("main").getString("temp");
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("main");
                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String result = String.format("Город: %s\nПогода: %s\nТемпература: %s℃", name, description, temp);
                if (weather.equals("Thunderstorm")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.thunder));
                } else if (weather.equals("Drizzle")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.drizzle));
                } else if (weather.equals("Rain")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.rain));
                } else if (weather.equals("Snow")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.snow));
                } else if (weather.equals("Clear")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.clear));
                } else if (weather.equals("Clouds")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.clouds));
                } else if (weather.equals("Mist")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.tuman));
                } else if (weather.equals("Haze")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.tuman));
                } else if (weather.equals("Fog")) {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.tuman));
                } else {
                    mainMenu.setBackground(ContextCompat.getDrawable(MainActivity.this, R.drawable.clear));
                }
                textView.setText(result);
                textView.setShadowLayer(3,10,5, R.color.black);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                Toast.makeText(MainActivity.this, "Не найдено!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void hideKeyboardFrom(Context context, View view) {       // Метод что бы убрать клавиатуру
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}