package eu.visiton.app.ui.pois.details;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;


public class SaveImageHelper implements Target {
    private Context context;
    private WeakReference<AlertDialog> alertDialogWeakReference;
    private WeakReference<ContentResolver> contentResolverWeakReference;
    private String name;
    private String desc;

    public SaveImageHelper(Context context, AlertDialog alertDialog, ContentResolver contentResolver, String name, String desc) {
        this.context = context;
        this.alertDialogWeakReference = new WeakReference<AlertDialog>(alertDialog);
        this.contentResolverWeakReference = new WeakReference<ContentResolver>(contentResolver);
        this.name = name;
        this.desc = desc;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        ContentResolver r = contentResolverWeakReference.get();
        AlertDialog dialog = alertDialogWeakReference.get();
        if (r != null){

            MediaStore.Images.Media.insertImage(r, bitmap, name, desc);
            Toast.makeText(context, "Image has been downloaded", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "The image was already downloaded", Toast.LENGTH_SHORT).show();

        }
        dialog.show();
        dialog.dismiss();




        //Intent intent = new Intent();
       // intent.setType("image/*");
       // intent.setAction(Intent.ACTION_GET_CONTENT);
        //context.startActivity(Intent.createChooser(intent, "VIEW PICTURE").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

    }






    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {

    }
}
