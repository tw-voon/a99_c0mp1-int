package kuchingitsolution.betterpepperboard.officer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.complaint.SingleReportActivity;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.ImageCompressionUtils;
import kuchingitsolution.betterpepperboard.helper.Utility;
import kuchingitsolution.betterpepperboard.helper.network.ApiCall;
import kuchingitsolution.betterpepperboard.helper.network.CountingRequestBody;
import kuchingitsolution.betterpepperboard.helper.network.RequestBuilder;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

public class OfficerActivity extends AppCompatActivity implements Spinner.OnItemSelectedListener{

    EditText edtAction;
    ImageView imgActionImage, closeImage;
    RadioGroup status_action;
    Button add_image;
    TextView tvImage;
    Button submit;
    Bitmap bitmap;
    ArrayList<String> statusData;
    File imgFile;
    private String report_id, action_taken, status_id, userChoosenTask, imagePath;
    private OkHttpClient client;
    private RelativeLayout relativeLayout;
    ImageCompressionUtils imageCompressionUtils;
    private Uri tempImgFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_officer);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        edtAction = findViewById(R.id.edtAction);
        add_image = findViewById(R.id.add_image);
        closeImage = findViewById(R.id.cancel_image);
        submit = findViewById(R.id.submit);
        status_action = findViewById(R.id.status);

        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(300, TimeUnit.SECONDS);
        b.writeTimeout(400, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(true);
        client = b.build();
        report_id = getIntent().getStringExtra("report_id");
        imgActionImage = findViewById(R.id.ivImg);
        relativeLayout = findViewById(R.id.image);
        imageCompressionUtils = new ImageCompressionUtils(this);
        status_id = "2";

        add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] items = { "Take Photo", "Choose from Library",
                        "Cancel" };
                AlertDialog.Builder builder = new AlertDialog.Builder(OfficerActivity.this);
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        boolean result= Utility.checkPermission(OfficerActivity.this);
                        if (items[item].equals("Take Photo")) {
                            userChoosenTask="Take Photo";
                            if(result)
                                cameraIntent();
                        } else if (items[item].equals("Choose from Library")) {
                            userChoosenTask="Choose from Library";
                            if(result)
                                galleryIntent();
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                            userChoosenTask="Cancel";
                        } else {
                            dialog.dismiss();
                            userChoosenTask="Cancel";
                        }
                    }
                });
                builder.show();
            }
        });

        status_action.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                switch (i){
                    case R.id.solved:
                        status_id = "1";
                        break;
                    case R.id.unsolve:
                        status_id = "2";
                        break;
                    default:
                        status_id = "2";
                        break;
                }
            }
        });

        closeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmap != null) {
                    bitmap.recycle();
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action_taken = edtAction.getText().toString().trim();
                uploadImage();
            }
        });
    }

    private void cameraIntent(){

        Intent intent;
        if(Build.VERSION.SDK_INT  < 24) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = Uri.fromFile(createImageFile());
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = FileProvider.getUriForFile(OfficerActivity.this, "kuchingitsolution.betterpepperboard.provider", getOutputMediaFile());
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgFile);
        startActivityForResult(intent, 1888);

    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Better City");

        if (!mediaStorageDir.exists()){
            boolean is_dir_make = mediaStorageDir.mkdirs();
            if (!is_dir_make){
                Log.d("error", "null thing");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private File createImageFile() {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/BetteCity");

        if(!storageDir.exists())
            storageDir.mkdirs();

        File images = null;
        try {
            images = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    private void galleryIntent(){

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select File"), 1);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("result code", " " + requestCode + "   " + resultCode);

        if (requestCode == 1888 && resultCode == RESULT_OK) {
            onCaptureImageResult(data);
        }

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            String path = imageCompressionUtils.compressImage(uri);
            try {
                Uri imageUri = data.getData();
                imgFile = new File(path);
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                bitmap = BitmapFactory.decodeStream(imageStream);
                bitmap = getResizedBitmap(bitmap, bitmap.getWidth() / 5, bitmap.getHeight() / 5);
                imgActionImage.setImageBitmap(bitmap);
                relativeLayout.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.d("bitmapE", String.valueOf(bitmap));
                e.printStackTrace();
            }
        }
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromURI_API11to18(Context context, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        String result = null;

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        if(cursor != null){
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            result = cursor.getString(column_index);
        }
        return result;
    }

    private void onCaptureImageResult(Intent data) {
        imagePath = tempImgFile.getPath();
        Log.d("TAG", "File Saved::--->" + tempImgFile.getPath());
        if(Build.VERSION.SDK_INT> 23) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImgFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            bitmap = BitmapFactory.decodeFile(imagePath);
        imagePath = imageCompressionUtils.saveImage(bitmap, this); /* change the image path to modified image */
        Log.d("TAG", "File Saved::--->" + imagePath);
        imgFile = new File(imagePath);
        Bitmap preview = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        imgActionImage.setImageBitmap(Bitmap.createScaledBitmap(preview, preview.getWidth() / 2 , preview.getHeight() / 2, false));
        relativeLayout.setVisibility(View.VISIBLE);
        imgActionImage.setVisibility(View.VISIBLE);
    }

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

        if(statusData.get(i).equals("Solved")){
            tvImage.setVisibility(View.VISIBLE);
        } else {
            imgActionImage.setVisibility(View.GONE);
            tvImage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @SuppressLint("StaticFieldLeak")
    private void uploadImage() {

        new AsyncTask<String, Integer, String>() {

            private AlertDialog.Builder alert;
            private AlertDialog ad;

            private ProgressBar dialog_upload;
            private TextView dialog_upload_status;
            private ImageView status_done;
            private AlertDialog upload_dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                alert = new AlertDialog.Builder(OfficerActivity.this);
                LayoutInflater inflater = OfficerActivity.this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.bottom_sheet_upload, null);

                dialog_upload = dialogView.findViewById(R.id.uploading);
                dialog_upload_status = dialogView.findViewById(R.id.uploading_progress);
                status_done = dialogView.findViewById(R.id.status_done);

                alert.setView(dialogView);
                alert.setCancelable(false);
                alert.create();
                ad = alert.show();
            }

            @Override
            protected String doInBackground(String... strings) {

                MultipartBody body = null;

                if(imgFile != null)
                    body = RequestBuilder.updateAction(report_id, imgFile, action_taken, status_id);
                else
                    body = RequestBuilder.update_action_no_image(report_id, action_taken, status_id);

                CountingRequestBody monitoring = new CountingRequestBody(body, new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(long bytesWritten, long contentLength) {

                        float percentage = 100f * bytesWritten / contentLength;
                        if (percentage >= 0) {
                            publishProgress((int)percentage);
                        }
                    }
                });

                try {
                    return ApiCall.POST(client, Config.URL_UPDATE_ACTION, monitoring);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                dialog_upload.setProgress(values[0]);
                dialog_upload_status.setText(String.valueOf(values[0]));
                if(values[0] == 100) {
                    status_done.setVisibility(View.VISIBLE);
                    dialog_upload.setIndeterminate(true);
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                Log.d("Result", s + " ");
//                showMessage(s);
                if(s != null) {
                    if (s.equals("success")) {
                        Intent intent = new Intent(OfficerActivity.this, SingleReportActivity.class);
                        intent.putExtra("action_taken", action_taken);
                        intent.putExtra("status_id", status_id);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                } else showMessage("Error while updating action, please try again");
                ad.dismiss();
            }
        }.execute();
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OfficerActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}
