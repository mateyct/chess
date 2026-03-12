package server;

import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

public class ServerFacade {
    // methods are currently stubbed
    String authToken;

    public LoginResult login(LoginRequest request) {
        authToken = request.password() + request.hashCode();
        return new LoginResult(request.username(), request.password() + request.hashCode());
    }

    public RegisterResult register(RegisterRequest request) {
        authToken = request.password() + request.email();
        return new RegisterResult(request.username(), request.password() + request.email());
    }

    public void logout(LogoutRequest request) {
        if (authToken.isEmpty()) {
            throw new RuntimeException("Cannot make request, not logged in.");
        }
    }
}
