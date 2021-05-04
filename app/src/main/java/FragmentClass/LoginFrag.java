package FragmentClass;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.qiva.jamuku.AuthActivity;
import com.qiva.jamuku.R;

import java.util.HashMap;


public class LoginFrag extends Fragment {
    public Activity activity;
    Button btn_login, btn_signup_go;
    EditText login_username, login_password;
    HashMap<String, String> param = new HashMap<>();
    public View view;
    public LoginFrag(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_login, container, false);
        btn_login = (Button) this.view.findViewById(R.id.btn_login);
        btn_signup_go = (Button) this.view.findViewById(R.id.btn_signup_go);
        login_username = (EditText) this.view.findViewById(R.id.login_username);
        login_password = (EditText) this.view.findViewById(R.id.login_password);

        logic();
        return this.view;
    }
    public void logic(){
        btn_signup_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthActivity) activity).register();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( login_username.getText().toString().length() <= 0){
                    login_username.setError("Please fill the field");
                }else{
                    if( login_password.getText().toString().length() <= 0){
                        login_password.setError("Please fill the field");
                    }else{
                        param.put("Request","login");
                        param.put("PUsername", login_username.getText().toString());
                        param.put("PPassword", login_password.getText().toString());
                        ((AuthActivity) activity).submitLoginData(param);
                    }
                }
            }
        });
    }

}