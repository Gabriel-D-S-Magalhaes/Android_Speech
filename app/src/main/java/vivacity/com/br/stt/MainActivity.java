package vivacity.com.br.stt;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private static final int RECOGNIZE_SPEECH = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvResult = (TextView) findViewById(R.id.tvResult);
    }

    public void getSpeechInput(View view) {

        // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#ACTION_RECOGNIZE_SPEECH
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //Use a language model based on free-form speech recognition. This is a value to use for EXTRA_LANGUAGE_MODEL.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // Optional text prompt to show to the user when asking them to speak.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Try say something");

        // Optional IETF language tag (as defined by BCP 47), for example "en-US".
        // This tag informs the recognizer to perform speech recognition in a language different than the one set in the getDefault().
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "pt-BR");// Portuguese Brazilian
        //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");// English US

        // {@link android.content.pm.PackageManager} : Essa classe recupera informações de vários
        // tipos sobre os pacotes da aplicação que estão instalados no dispositivo.

        // getPackageManager = Para descobrir se a captação de áudio do dispositivo móvel está realmente ativa.

        /**
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RECOGNIZE_SPEECH);
        } else {
            Toast.makeText(MainActivity.this, "Your device don't support Speech Input", Toast.LENGTH_LONG).show();
        }
         or use the following try catch
         */

        // NOTE: There may not be any applications installed to handle this action, so you should make sure to catch ActivityNotFoundException.
        // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#ACTION_RECOGNIZE_SPEECH
        try {
            startActivityForResult(intent, RECOGNIZE_SPEECH);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Your device don't support Speech Input", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECOGNIZE_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#EXTRA_RESULTS
                    ArrayList<String> result =  data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvResult.setText(result.get(0));
                }
                break;
        }
    }
}
