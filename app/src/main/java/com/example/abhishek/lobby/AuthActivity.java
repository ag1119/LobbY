package com.example.abhishek.lobby;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

/**
 * Created by R K Rai on 30-03-2018.
 */

public class AuthActivity extends AppCompatActivity {

    public static final String PREF="username";
    public static final String PREF1="myNumber";
    private EditText mPhonefield,mOtpfield;
    private Button mLoginbtn;
    private FirebaseAuth mAuth;
    private int btntyp=0;
    private int s=0;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private TextView resendOtp;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    ProgressDialog progressDialog;
    String number,username,myNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        SharedPreferences sf=getSharedPreferences(PREF,MODE_PRIVATE);
        username=sf.getString("username","null");

        if(username!=null)
            Log.v("username is :",username);

        mPhonefield = (EditText) findViewById(R.id.mobileNo);
        mLoginbtn = (Button) findViewById(R.id.continueBtn);
        resendOtp=(TextView)findViewById(R.id.resend_otp);
        mOtpfield = (EditText) findViewById(R.id.otp);
        mAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(AuthActivity.this);

        mLoginbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(btntyp==0)
                { number = mPhonefield.getText().toString();
                    if(number.length()!=10 && number.length()>0)
                    {
                        mPhonefield.setError("Invalid Phone Number.");
                    }
                    else if(TextUtils.isEmpty(number))
                    {
                        mPhonefield.setError("Cannot be empty");
                    }
                    else
                    {
                        mLoginbtn.setEnabled(false);
                        mPhonefield.setEnabled(false);
                        mOtpfield.setVisibility(View.VISIBLE);
                        resendOtp.setVisibility(View.VISIBLE);



                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                "+91"+    number,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                AuthActivity.this,
                                mCallbacks// Activity (for callback binding)

                        );
                    }}
                else
                {s++;
                    if(s<=5)
                    {
                        mLoginbtn.setEnabled(true);
                        String verifyotp=mOtpfield.getText().toString();
                        if(TextUtils.isEmpty(verifyotp))
                        {
                            mOtpfield.setError("Cannot be empty.");

                        }
                        else
                        {
                            progressDialog.setMessage("Verifying number...");
                            progressDialog.show();
                            PhoneAuthCredential credent=PhoneAuthProvider.getCredential(mVerificationId,verifyotp);
                            signInWithPhoneAuthCredential(credent);

                        }}
                    else
                    {
                        mLoginbtn.setEnabled(false);
                        mOtpfield.setError("Too many attempts");
                        Toast.makeText(AuthActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                    }}





            }
        });
        mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
        { @Override
        public void onVerificationCompleted(PhoneAuthCredential credential) {
            signInWithPhoneAuthCredential(credential);

        }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(AuthActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();


            }
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Toast.makeText(AuthActivity.this, "Code Sent", Toast.LENGTH_SHORT).show();
                btntyp=1;
                mLoginbtn.setText("Verify Code");
                mLoginbtn.setEnabled(true);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // ...
            }


        };

      resendOtp.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View v) {
              String number=mPhonefield.getText().toString();
               resent(number,mResendToken);
          }
      });




    }
public void resent(String number,PhoneAuthProvider.ForceResendingToken tokenn)
{
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
            "+91"+    number,        // Phone number to verify
            60,                 // Timeout duration
            TimeUnit.SECONDS,   // Unit of timeout
            AuthActivity.this,
            mCallbacks,// Activity (for callback binding)
tokenn
    );



}




    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(AuthActivity.this, "Sign In Successful", Toast.LENGTH_SHORT).show();
                            FirebaseUser user = task.getResult().getUser();

                            SharedPreferences myNumber=getSharedPreferences(PREF1,MODE_PRIVATE);
                            SharedPreferences.Editor editor=myNumber.edit();
                            editor.putString("myNumber",mPhonefield.getText().toString());
                            editor.commit();
                                Intent intent=new Intent(AuthActivity.this,UserName.class);
                                intent.putExtra("mobileNo",number);
                                finish();
                                startActivity(intent);



                            progressDialog.dismiss();
                                           // ...
                        } else {
                            // Sign in failed, display a message and update the UI

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                progressDialog.dismiss();
                                mOtpfield.setError("Invalid otp");// The verification code entered was invalid
                            }
                            Toast.makeText(AuthActivity.this, "Sign in Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }












}
