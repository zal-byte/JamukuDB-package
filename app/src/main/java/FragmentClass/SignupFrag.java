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


public class SignupFrag extends Fragment {
    public Activity activity;
    EditText signup_username, signup_name, signup_email,signup_phone, signup_address, signup_password, signup_password_verify;
    Button btn_signup, btn_login_go;
    HashMap<String, String> param = new HashMap<>();
    public View view;
    public SignupFrag(Activity activity) {
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
        this.view = inflater.inflate(R.layout.fragment_signup, container, false);
        init();
        logic();
        return this.view;
    }
    public void init(){
        signup_username = (EditText) this.view.findViewById(R.id.signup_username);
        signup_name = (EditText) this.view.findViewById(R.id.signup_name);
        signup_email = (EditText) this.view.findViewById(R.id.signup_email);
        signup_phone = (EditText) this.view.findViewById(R.id.signup_phone);
        signup_address = (EditText) this.view.findViewById(R.id.signup_address);
        signup_password = (EditText) this.view.findViewById(R.id.signup_password);
        signup_password_verify = (EditText) this.view.findViewById(R.id.signup_password_verify);

        btn_signup = (Button) this.view.findViewById(R.id.btn_signup);
        btn_login_go = (Button) this.view.findViewById(R.id.btn_login_go);
    }
    public void logic(){
        btn_login_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AuthActivity) activity).login();
            }
        });
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }
        });
    }
    public void check(){
        if( widgetLength(signup_username) <= 0){
            signup_username.setError("Please fill the field");
        }else{
            if( widgetLength(signup_name) <= 0){
                signup_name.setError("Please fill the field");
            }else{
                if( widgetLength(signup_email) <= 0){
                    signup_email.setError("Please fill the field");
                }else{
                    if( widgetLength(signup_phone) <= 10){
                        signup_phone.setError("Please input your phone number");
                    }else{
                        if( widgetLength(signup_address) <= 0){
                            signup_address.setError("Please fill the field");
                        }else{
                            if( widgetLength(signup_password) <= 0){
                                signup_password.setError("Please fill the field");
                            }else{
                               if( !(widgetString(signup_password_verify).equals(widgetString(signup_password)))){
                                   signup_password_verify.setError("Password Doesn't Match");
                               }else{
                                   param.put("Request","signup");
                                   param.put("PUsername", widgetString(signup_username));
                                   param.put("PName", widgetString(signup_name));
                                   param.put("PEmail", widgetString(signup_email));
                                   param.put("PPhone", widgetString(signup_phone));
                                   param.put("PAddress", widgetString(signup_address));
                                   param.put("PPassword", widgetString(signup_password));

                                   ((AuthActivity) activity).submitSignupData(param);
                               }
                            }
                        }
                    }
                }
            }
        }
    }
    public int widgetLength(EditText editText){
        return editText.getText().toString().length();
    }
    public String widgetString(EditText editText){
        return editText.getText().toString();
    }

}