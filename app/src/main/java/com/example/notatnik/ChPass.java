package com.example.notatnik;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Random;



public class ChPass extends AppCompatActivity {
    DatabaseHelper myDB;
    EditText editText1,editText2;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chpass);
        myDB = new DatabaseHelper(this);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        button = (Button) findViewById(R.id.button);
        final Crpass pass = new Crpass();
        final PasswordStorage pasy = new PasswordStorage();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text1 = editText1.getText().toString();
                String text2 = editText2.getText().toString();
                if(text1.equals("") || text2.equals("")){
                    Toast.makeText(ChPass.this, "Nie wpisano hasła", Toast.LENGTH_LONG).show();
                } else{
                    String pom = pasy.checkPassword(text2);
                    String pomo = pasy.checkPassword(text1);
                    if (pom == "true" && pomo == "true") {
                    if(text1.equals(text2)){
                            String passhash;
                            try {
                                byte[] sole = pasy.salte();
                                String sola = Base64.getEncoder().encodeToString(sole);
                                passhash = PasswordStorage.createHash(sole,text2);
                                SharedPreferences settings = getSharedPreferences("PREFS",0);
                                SharedPreferences.Editor editor = settings.edit();
                                editor.putString("sol", sola);
                                editor.putString("hasło", passhash);
                                editor.apply();
                                Toast.makeText(ChPass.this, "Zmiana hasla", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            } catch (PasswordStorage.CannotPerformOperationException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                    }
                        else {
                        Toast.makeText(ChPass.this, "hasla nie pasuja do siebie", Toast.LENGTH_LONG).show();

                        }
                    } else {
                        Toast.makeText(ChPass.this, pom, Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


    }
    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore");
        kpg.initialize(new KeyGenParameterSpec.Builder(
                "alias",
                KeyProperties.PURPOSE_SIGN | KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256,
                        KeyProperties.DIGEST_SHA512)
                .build());

        KeyPair kp = kpg.generateKeyPair();
        return kp;
    }

}
