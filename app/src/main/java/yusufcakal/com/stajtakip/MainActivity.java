package yusufcakal.com.stajtakip;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import yusufcakal.com.stajtakip.pojo.User;
import yusufcakal.com.stajtakip.webservices.interfaces.LoginListener;
import yusufcakal.com.stajtakip.webservices.services.LoginService;
import yusufcakal.com.stajtakip.webservices.util.SessionUtil;

public class MainActivity extends AppCompatActivity
    implements View.OnClickListener, LoginListener{

    private EditText etEmail, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (SessionUtil.check(this))
            startActivity(new Intent(this, DashboardActivity.class));

        setTitle(getResources().getText(R.string.login));

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        
        btnLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        loginService(user);

    }

    private void loginService(User user){
        LoginService loginService = new LoginService(this, this);
        loginService.login(user);
    }


    @Override
    public void onSuccess(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            boolean loginFlag = jsonObject.getBoolean("result");
            if (loginFlag){
                JSONObject info = jsonObject.getJSONObject("bilgiler");
                String token = info.getString("token");
                SessionUtil.start(this, token);
                startActivity(new Intent(this, DashboardActivity.class));
            }else{
                String error = jsonObject.getString("error");
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(VolleyError error) {
        Toast.makeText(this, String.valueOf(error), Toast.LENGTH_SHORT).show();
    }
}
