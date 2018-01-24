package vivacity.com.br.stt;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
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
    private static final int MY_DATA_CHECK_CODE = 20;
    private TextToSpeech textToSpeech;
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

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

        //

        //if (intent.resolveActivity(getPackageManager()) != null) {

        //startActivityForResult(intent, RECOGNIZE_SPEECH);

        //} else {

        //Toast.makeText(MainActivity.this, "Your device don't support Speech Input", Toast.LENGTH_LONG).show();

        //}

        //or use the following try catch

        //

        // NOTE: There may not be any applications installed to handle this action, so you should make sure to catch ActivityNotFoundException.
        // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#ACTION_RECOGNIZE_SPEECH
        try {
            startActivityForResult(intent, RECOGNIZE_SPEECH);
        } catch (ActivityNotFoundException e) {

            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Your device don't support Speech Input", Toast.LENGTH_LONG).show();

            // Google App is necessary!
            // Link to download apk: https://www.apkmirror.com/apk/google-inc/google-search/
            String appPackageName = "com.google.android.googlequicksearchbox";
            try {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));

            } catch (ActivityNotFoundException anfe) {

                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
    }

    /**
     * Check for the presence of the TTS resources with the corresponding intent:
     */
    public void textToSpeak(View view) {

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECOGNIZE_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    // See: https://developer.android.com/reference/android/speech/RecognizerIntent.html#EXTRA_RESULTS
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvResult.setText(result.get(0));
                    setText(result.get(0));
                }
                break;

            case MY_DATA_CHECK_CODE:
                // A successful check will be marked by a CHECK_VOICE_DATA_PASS result code,
                // indicating this device is ready to speak ...
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {

                    // after the creation of our android.speech.tts.TextToSpeech object.
                    // success, create the TTS instance
                    textToSpeech = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {

                        @Override
                        public void onInit(int status) {

                            if (status == TextToSpeech.SUCCESS) {

                                if (textToSpeech.isLanguageAvailable(new Locale("pt", "BR")) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {

                                    // The specified language as represented by the Locale is available and supported.
                                    textToSpeech.setLanguage(new Locale("pt", "BR"));

                                    //setText("Olá"); <-- Test (in Portuguese Brazilian) here
                                    if (getText() != null) {

                                        if (getText().length() <= TextToSpeech.getMaxSpeechInputLength()) {

                                            System.out.println("Text length = " + getText().length());
                                            textToSpeech.speak("Você disse: " + getText(), TextToSpeech.QUEUE_FLUSH, null);
                                        } else {

                                            System.out.println("Limit of length of input string passed to speak and synthesizeToFile = " + TextToSpeech.getMaxSpeechInputLength());
                                            Toast.makeText(MainActivity.this, "Limit of length of input string passed to speak and synthesizeToFile = " + TextToSpeech.getMaxSpeechInputLength(), Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Sem texto a ser sintetizado", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "Your device don't support Speech output", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {

                    // We need to let the user know to install the data that's required for the
                    // device to become a multi-lingual talking machine! Downloading and installing
                    // the data is accomplished by firing off the ACTION_INSTALL_TTS_DATA intent,
                    // which will take the user to Android Market, and will let her/him initiate the
                    // download. Installation of the data will happen automatically once the
                    // download completes.
                    // missing data, install it
                    Intent installIntent = new Intent();
                    installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (textToSpeech != null) {
                textToSpeech.stop();
                textToSpeech.shutdown();
                System.out.println("shutdown() invoked in method onDestroy()");
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}
