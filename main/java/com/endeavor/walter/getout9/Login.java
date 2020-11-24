package com.endeavor.walter.getout9;

import java.io.Serializable;


public class Login implements Serializable {
    private long mLoginId;
    private String mUsername;
    private String mEmail;

    public static final String TAG = Note.class.getSimpleName();

    public Login(Integer id, String username, String email){
//      wvs why pass in Integer and not long? 7/20/20
        this.mLoginId = id;
        this.mUsername = username;
        this.mEmail = email;
    }

    public Long getLoginId() {
        return mLoginId;
    }

    public void setLoginId(long loginId){
        this.mLoginId= loginId;
    }


    public String getUsername(){
        return mUsername;
    }

    public void setUsername(String username){
        this.mUsername = username;
    }

    public String getEmail(){
        return mEmail;
    }

    public void setEmail(String email){
        this.mEmail = email;
    }

}
