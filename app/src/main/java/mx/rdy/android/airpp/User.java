package mx.rdy.android.airpp;

import java.io.Serializable;

/**
 * Created by Seca on 9/7/15.
 */
public class User implements Serializable {
    private int mId;
    private String mName;
    private String mPass;

    public String getPass() {
        return mPass;
    }

    public void setPass(String pass) {
        mPass = pass;
    }

    private String mStatus;

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }
}
