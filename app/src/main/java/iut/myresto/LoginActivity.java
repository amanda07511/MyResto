package iut.myresto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.auth0.android.jwt.Claim;
import com.auth0.android.jwt.JWT;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import iut.myresto.models.User;
import iut.myresto.tools.CameraHandler;
import iut.myresto.tools.db.DatabaseHandler;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private EditText mNomView;
    private EditText mPrenomView;
    private ImageButton mAvatar;
    Button mEmailSignInButton;
    private View mProgressView;
    private View mLoginFormView;

    //Variables
    private String name;
    private String token;
    private String url = "https://myrestoapp.herokuapp.com/user/";
    private int mStatusCode;
    //Components
    private DatabaseHandler database;
    private  User user;
    private  User  u;
    CameraHandler camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Adding Back button
        getSupportActionBar().setHomeButtonEnabled(true);

        //Gettin Intent Information
        Bundle extras = getIntent().getExtras();
        name = extras.getString("Type");

        // Set up the login form.
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mEmailView = (EditText) findViewById(R.id.email);
        mNomView = (EditText) findViewById(R.id.nom);
        mPrenomView = (EditText) findViewById(R.id.prenom);
        mAvatar = (ImageButton) findViewById(R.id.avatar);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.equals("Login")||name.equals("Signup"))
                    attemptLogin();
                else
                    attemptEdit();
            }
        });

        if(name.equals("Login")) {
            mNomView.setVisibility(View.GONE);
            mPrenomView.setVisibility(View.GONE);
            mAvatar.setVisibility(View.GONE);
            mEmailSignInButton.setText(getResources().getText(R.string.action_login));
            this.setTitle(getResources().getText(R.string.title_activity_login));
        } else if(name.equals("Signup")) {
            mNomView.setVisibility(View.VISIBLE);
            mPrenomView.setVisibility(View.VISIBLE);
            mAvatar.setVisibility(View.GONE);
            mEmailSignInButton.setText(getResources().getText(R.string.action_sign_in));
            this.setTitle(getResources().getText(R.string.title_activity_signup));
        } else if(name.equals("Edit")) {
            myAccount();
        }

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (name.equals("Edit")) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_select, menu);
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (item.getItemId() == android.R.id.home ) {
            Intent i=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();

        } else if (id == R.id.edit) {
            mNomView.setEnabled(true);
            mPrenomView.setEnabled(true);
            mAvatar.setEnabled(true);
            mEmailSignInButton.setEnabled(true);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mNomView.setError(null);
        mPrenomView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String nom = mNomView.getText().toString();
        String prenom = mPrenomView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(name.equals("Signup")){
            // Check for a valid nom
            if (TextUtils.isEmpty(nom)) {
                mNomView.setError(getString(R.string.error_field_required));
                focusView = mNomView;
                cancel = true;
            }
            // Check for a valid prenom
            if (TextUtils.isEmpty(prenom)) {
                mPrenomView.setError(getString(R.string.error_field_required));
                focusView = mPrenomView;
                cancel = true;
            }

        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                if(name.equals("Login")) {
                    loginRequest(url);
                } else if (name.equals("Signup")) {
                    singUpRequest(url);
                }

                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }


            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(i);
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    private void loginRequest(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, url+"login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                try {
                    JSONObject jsonRespuesta = new JSONObject(response);
                    token = jsonRespuesta.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Log.d("Respuesta! :D", response);
                Log.d("Token",token);
                JWT parsedJWT = new JWT(token);
                Claim subscriptionMetaData = parsedJWT.getClaim("id");
                String parsedValue = subscriptionMetaData.asString();
                Log.d("ID",parsedValue);
                getUser("https://myrestoapp.herokuapp.com/user/");

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    showProgress(false);
                    ErrorMessage();
                } else {
                    Log.d("Respuesta! D:", error.getMessage());
                }

            }
        }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);


    }

    private void getUser(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.GET, url+"get", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                Gson gson = new Gson();
                user = gson.fromJson(response, User.class);

                //Getting kept data of the database
                database = new DatabaseHandler(LoginActivity.this);
                database.open();
                database.addObj(1,token, user);
                database.close();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    showProgress(false);
                    ErrorMessage();
                } else {
                    Log.d("Respuesta! D:", error.getMessage());
                }

            }
        }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("token",token);
                return params;
            }
        };
        queue.add(sr);


    }

    private void singUpRequest(String url) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, url+"signup", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Convert the response to a JSONObject
                try {
                    JSONObject jsonRespuesta = new JSONObject(response);
                    token = jsonRespuesta.getString("token");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d("Token",token);
                JWT parsedJWT = new JWT(token);
                Claim subscriptionMetaData = parsedJWT.getClaim("id");
                String parsedValue = subscriptionMetaData.asString();
                Log.d("ID",parsedValue);
                getUser("https://myrestoapp.herokuapp.com/user/");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    showProgress(false);
                    ErrorEmail();
                } else {
                    Log.d("Respuesta! D:", error.getMessage());
                }

            }
        }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());
                params.put("nom", mNomView.getText().toString());
                params.put("prenom", mPrenomView.getText().toString());
                params.put("photo", " ");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);


    }

    private void userUpdate(String url) {

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest sr = new StringRequest(Request.Method.POST, url+"update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Update", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if (error.networkResponse.headers.containsValue("NETWORK 404")) {
                    showProgress(false);
                    ErrorEmail();
                }
                if (error.networkResponse.headers.containsValue("NETWORK 401")) {
                    showProgress(false);
                    ErrorEmail();
                }
                else {
                    Log.d("Respuesta! D:", error.getMessage());
                }

            }
        }){

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                mStatusCode = response.statusCode;
                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("nom", mNomView.getText().toString());
                params.put("prenom", mPrenomView.getText().toString());
                params.put("photo", " ");

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                params.put("Token",token);
                return params;
            }
        };
        queue.add(sr);


    }

    public void ErrorMessage() {

        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Invalid");
        alertDialog.setMessage("The Email or password inserted was wrong!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void ErrorEmail() {

        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
        alertDialog.setTitle("Invalid");
        alertDialog.setMessage("That email is already in use!");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void myAccount(){
        //Getting kept data of the database
        database = new DatabaseHandler(this);
        database.open();
        u = database.getUser();
        token = database.getToken();
        database.close();

        mNomView.setVisibility(View.VISIBLE);
        mPrenomView.setVisibility(View.VISIBLE);
        mAvatar.setVisibility(View.VISIBLE);
        mPasswordView.setVisibility(View.GONE);

        mNomView.setEnabled(false);
        mPrenomView.setEnabled(false);
        mAvatar.setEnabled(false);
        mEmailView.setEnabled(false);
        mPasswordView.setEnabled(false);
        mEmailSignInButton.setEnabled(false);

        mEmailSignInButton.setText(getResources().getText(R.string.action_modify));
        this.setTitle(getResources().getText(R.string.title_activity_account));
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        if(u!=null){
            mNomView.setText(u.getNom());
            mPrenomView.setText(u.getPrenom());
            mEmailView.setText(u.getEmail());
            Bitmap image = decode (u.getPhoto());
            mAvatar.setImageBitmap(image);
        }
    }

    private void attemptEdit() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mNomView.setError(null);
        mPrenomView.setError(null);

        String nom = mNomView.getText().toString();
        String prenom = mPrenomView.getText().toString();
        Log.d("Nom", nom);

        boolean cancel = false;
        View focusView = null;

        // Check for a valid nom
        if (TextUtils.isEmpty(nom)) {
            mNomView.setError(getString(R.string.error_field_required));
            focusView = mNomView;
            cancel = true;
        }
        // Check for a valid prenom
        if (TextUtils.isEmpty(prenom)) {
            mPrenomView.setError(getString(R.string.error_field_required));
            focusView = mPrenomView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            showProgress(true);
            u.setNom(nom);
            u.setPrenom(prenom);
            userUpdate("https://myrestoapp.herokuapp.com/user/");
            showProgress(false);

            //Getting kept data of the database
            database = new DatabaseHandler(this);
            database.open();
            database.removeObj(1);
            database.addObj(1,token, u);
            database.close();

            myAccount();

        }
    }

    private String encode(String pathOfYourImage){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeFile(pathOfYourImage);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageString;
    }

    private Bitmap decode(String imageString){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }
}

