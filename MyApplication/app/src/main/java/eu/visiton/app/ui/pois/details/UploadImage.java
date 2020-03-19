package eu.visiton.app.ui.pois.details;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.squareup.picasso.Picasso;

import java.util.UUID;

import dmax.dialog.SpotsDialog;

public class UploadImage extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private Context ctx;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
            {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this.ctx, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this.ctx, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }


    protected void checkPerm(Context ctx2) {


        this.ctx = ctx2;



        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) ;
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, PERMISSION_REQUEST_CODE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You should grant permissions", Toast.LENGTH_SHORT).show();
            requestPermissions(new String[]{
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
            }, PERMISSION_REQUEST_CODE);
            return;
        } else {
            AlertDialog dialog = new SpotsDialog((Activity)this.ctx);
            dialog.show();
            dialog.setMessage("Downloading ...");

            String fileName = UUID.randomUUID().toString() + ".jpg";
            /*Picasso.with(getBaseContext())
                    .load("https://www.pastafarismo.es/wp-content/uploads/SistineHirez3-tentative-1024x495.jpg")
                    .into(new SaveImageHelper(getBaseContext(),
                            dialog,
                            getApplicationContext().getContentResolver(),
                            fileName,
                            "Image description"));*/


        }
    }


    }