package com.filelocater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by ADMIN on 23-04-2018.
 */

public abstract class PermissionActivity extends AppCompatActivity {

    String[] perms = {"android.permission.READ_EXTERNAL_STORAGE","android.permission.WRITE_EXTERNAL_STORAGE"};
    int permsRequestCode = 200;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("NewApi")
    public void requestPermissionAction(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(perms, permsRequestCode);
            }else {
                onPermissionsGranted(1);
            }
        }else {
            onPermissionsGranted(1);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch(permsRequestCode){

            case 200:

                boolean getAccount = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                if(getAccount){
                    onPermissionsGranted(1);
                }else {
                    Toast.makeText(this, "We cannot proceed without accepting the permission.", Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }
    /* */
    private boolean canMakeSmores(){

        return(Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP_MR1);

    }

    private boolean hasPermission(String permission){

        if(canMakeSmores()){

            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);

        }

        return true;

    }

    public int checkSelfPermission(String permission) {
        return 1;
    }

    public abstract void onPermissionsGranted(int requestCode);
}