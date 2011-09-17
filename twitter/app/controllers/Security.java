package controllers;
import models.User;

public class Security extends Secure.Security {
    static boolean authenticate(String username, String password) {
        User user = User.find("byUsername", username).first();
        return user != null && user.password.equals(password);
    }

    static boolean check(String profile) {
        User user = userConnected();
        if ("isAdmin".equals(profile)) {
            return user.admin;
        } else {
            return false;
        }
    } 
    
    static User userConnected(){
        User user = User.find("byUsername", session.get("username")).first();
        return user;
    }   
}