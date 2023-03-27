package com.client11;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.DataCache;
import Data.ServerProxy;
import Models.Person;
import Request.LoginRequest;
import Request.RegisterRequest;
import Result.FindFamilyEventsResult;
import Result.FindFamilyResult;
import Result.LoginResult;
import Result.RegisterResult;

public class LoginFragment extends Fragment {

    private static Listener listener;

    public interface Listener {
        void notifyDone();
    }

    public void registerListener(Listener listener) {
        this.listener = listener;
    }

    private static final String LOGIN_RESULT = "LoginResult";
    private static final String REGISTER_RESULT = "RegisterResult";

    private EditText editTextServerHost;
    private EditText editTextServerPort;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextEmail;

    private RadioGroup radioGenderGroup;
    private String gender = "m";

    private Button login;
    private Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextServerHost = view.findViewById(R.id.serverHostField);
        editTextServerPort = view.findViewById(R.id.serverPortField);
        editTextUsername = view.findViewById(R.id.usernameField);
        editTextPassword = view.findViewById(R.id.passwordField);
        editTextFirstName = view.findViewById(R.id.firstNameField);
        editTextLastName = view.findViewById(R.id.lastNameField);
        editTextEmail = view.findViewById(R.id.emailAddressField);

        radioGenderGroup = view.findViewById(R.id.radioGender);
        radioGenderGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioMale) {
                gender = "m";
            } else if (checkedId == R.id.radioFemale) {
                gender = "f";
            }
        });

        login = view.findViewById(R.id.signInButton);
        register = view.findViewById(R.id.registerButton);

        editTextServerHost.addTextChangedListener(textWatcher);
        editTextServerPort.addTextChangedListener(textWatcher);
        editTextUsername.addTextChangedListener(textWatcher);
        editTextPassword.addTextChangedListener(textWatcher);
        editTextFirstName.addTextChangedListener(textWatcher);
        editTextLastName.addTextChangedListener(textWatcher);
        editTextEmail.addTextChangedListener(textWatcher);

        login.setOnClickListener(loginClicked);
        register.setOnClickListener(registerClicked);

        editTextServerHost.setText("10.0.2.2");
        editTextServerPort.setText("8080");
        editTextUsername.setText("cordellt");
        editTextPassword.setText("password");
        editTextFirstName.setText("Cordell");
        editTextLastName.setText("Thompson");
        editTextEmail.setText("cordellt2@gmail.com");

        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String serverHostInput = editTextServerHost.getText().toString().trim();
            String serverPortInput = editTextServerPort.getText().toString().trim();
            String usernameInput = editTextUsername.getText().toString().trim();
            String passwordInput = editTextPassword.getText().toString().trim();
            String firstNameInput = editTextFirstName.getText().toString().trim();
            String lastNameInput = editTextLastName.getText().toString().trim();
            String emailInput = editTextEmail.getText().toString().trim();

            login.setEnabled(!serverHostInput.isEmpty() && !serverPortInput.isEmpty()
                    && !usernameInput.isEmpty() && !passwordInput.isEmpty());
            register.setEnabled(!serverHostInput.isEmpty() && !serverPortInput.isEmpty()
                    && !usernameInput.isEmpty() && !passwordInput.isEmpty()
                    && !firstNameInput.isEmpty() && !lastNameInput.isEmpty()
                    && !emailInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };

    private View.OnClickListener loginClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        //display toasts according to success or failure
                        String result = bundle.getString(LOGIN_RESULT);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                };
                LoginRequest request = new LoginRequest(editTextUsername.getText().toString(),
                        editTextPassword.getText().toString());
                LoginTask task = new LoginTask(uiThreadMessageHandler, request,
                        editTextServerHost.getText().toString(), editTextServerPort.getText().toString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private View.OnClickListener registerClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                Handler uiThreadMessageHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message message) {
                        Bundle bundle = message.getData();
                        //display toasts according to success or failure
                        String result = bundle.getString(REGISTER_RESULT);
                        Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                    }
                };
                RegisterRequest request = new RegisterRequest(editTextUsername.getText().toString(),
                        editTextPassword.getText().toString(), editTextEmail.getText().toString(),
                        editTextFirstName.getText().toString(), editTextLastName.getText().toString(),
                        gender);
                RegisterTask task = new RegisterTask(uiThreadMessageHandler, request,
                        editTextServerHost.getText().toString(), editTextServerPort.getText().toString());

                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.submit(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static class LoginTask implements Runnable {

        private final Handler messageHandler;
        private final LoginRequest request;
        private final String serverHost;
        private final String serverPort;

        public LoginTask(Handler messageHandler, LoginRequest request, String serverHost,
                         String serverPort) {
            this.messageHandler = messageHandler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            ServerProxy serverProxy = new ServerProxy();
            serverProxy.ServerProxy(serverHost, serverPort);

            LoginResult result = serverProxy.login(request);

            if (result.isSuccess()) {
                FindFamilyResult findFamilyResult = serverProxy.findFamily(result.getAuthtoken());
                FindFamilyEventsResult findFamilyEventsResult = serverProxy.findFamilyEvents(result.getAuthtoken());

                DataCache dataCache = DataCache.getInstance();
                dataCache.addPeople(findFamilyResult.getData());
                dataCache.addEvents(findFamilyEventsResult.getData());
                dataCache.setAuthToken(result.getAuthtoken());
            }

            sendMessage(result);

            listener.notifyDone();
        }

        private void sendMessage(LoginResult result) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            if (result.isSuccess()) {
                DataCache dataCache = DataCache.getInstance();
                Person person = dataCache.getPerson(result.getPersonID());
                String personName = person.getFirstName() + " " + person.getLastName();
                messageBundle.putString(LOGIN_RESULT, personName);
            } else {
                String failed = "Login Failed";
                messageBundle.putString(LOGIN_RESULT, failed);
            }
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }

    private static class RegisterTask implements Runnable {

        private final Handler messageHandler;
        private final RegisterRequest request;
        private final String serverHost;
        private final String serverPort;

        public RegisterTask(Handler messageHandler, RegisterRequest request, String serverHost,
                            String serverPort) {
            this.messageHandler = messageHandler;
            this.request = request;
            this.serverHost = serverHost;
            this.serverPort = serverPort;
        }

        @Override
        public void run() {
            ServerProxy serverProxy = new ServerProxy();
            serverProxy.ServerProxy(serverHost, serverPort);

            RegisterResult result = serverProxy.register(request);

            if (result.isSuccess()) {
                FindFamilyResult findFamilyResult = serverProxy.findFamily(result.getAuthtoken());
                FindFamilyEventsResult findFamilyEventsResult = serverProxy.findFamilyEvents(result.getAuthtoken());

                DataCache dataCache = DataCache.getInstance();
                dataCache.addPeople(findFamilyResult.getData());
                dataCache.addEvents(findFamilyEventsResult.getData());
                dataCache.setAuthToken(result.getAuthtoken());
            }

            sendMessage(result);

            listener.notifyDone();
        }

        private void sendMessage(RegisterResult result) {
            Message message = Message.obtain();

            Bundle messageBundle = new Bundle();
            if (result.isSuccess()) {
                DataCache dataCache = DataCache.getInstance();
                Person person = dataCache.getPerson(result.getPersonID());
                String personName = person.getFirstName() + " " + person.getLastName();
                messageBundle.putString(REGISTER_RESULT, personName);
            } else {
                String failed = "Registration Failed";
                messageBundle.putString(REGISTER_RESULT, failed);
            }
            message.setData(messageBundle);

            messageHandler.sendMessage(message);
        }
    }
}