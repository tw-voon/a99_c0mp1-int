package kuchingitsolution.betterpepperboard.new_complaint;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.appcompat.BuildConfig;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kuchingitsolution.betterpepperboard.MainActivity2;
import kuchingitsolution.betterpepperboard.R;
import kuchingitsolution.betterpepperboard.helper.Config;
import kuchingitsolution.betterpepperboard.helper.ImageCompressionUtils;
import kuchingitsolution.betterpepperboard.helper.RealPathUtils;
import kuchingitsolution.betterpepperboard.helper.Session;
import kuchingitsolution.betterpepperboard.helper.Utility;
import kuchingitsolution.betterpepperboard.helper.network.ApiCall;
import kuchingitsolution.betterpepperboard.helper.network.CountingRequestBody;
import kuchingitsolution.betterpepperboard.helper.network.RequestBuilder;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;

import static android.R.attr.bitmap;
import static kuchingitsolution.betterpepperboard.R.id.image;
import static kuchingitsolution.betterpepperboard.R.id.loading;

public class NewComplaintActivity extends AppCompatActivity {

    TextView category, location;
    EditText edttitle, edtdesc, edtSuggestion;
    ImageView image_preview, locationPreview;
    String selectedLatitute, selectedLongitute, userLocation, userChoosenTask = "", userID, categoryID = null, imagePath;
    String title, desc, suggestion;
    final static int PLACE_PICKER_CODE = 1000, REQUEST_CAMERA = 1888, PICK_IMAGE_REQUEST = 2;
    Bitmap selectedImage;
    private OkHttpClient client;
    ProgressBar loading;
    Session session;
    File imgFile;
    Uri tempImgFile;
    ImageCompressionUtils imageCompressionUtils;
    private static final String IMAGE_DIRECTORY = "/Better City";

    private static final String CATEGORY = "category", CHOSEN_IMAGE = "chosen_image", TITLE = "title", DESC = "desc", IMAGE = "image", LOCATION = "location", CHOSEN_TASK = "chosen_task";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complaint);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        category =  findViewById(R.id.category);
        location =  findViewById(R.id.location);
        edttitle =  findViewById(R.id.report_title);
        edtdesc =  findViewById(R.id.report_desc);
        edtSuggestion = findViewById(R.id.report_suggestion);
        image_preview =  findViewById(R.id.image_preview);
        locationPreview =  findViewById(R.id.location_preview);
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(200, TimeUnit.SECONDS);
        b.writeTimeout(300, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(false); // Don't retry the connection (prevent twice entry)
        client = b.build();
        loading = findViewById(R.id.uploading);
        session = new Session(this);
        imageCompressionUtils = new ImageCompressionUtils(this);

        if(savedInstanceState != null){

            tempImgFile = savedInstanceState.getParcelable("imgFile");
            imagePath = savedInstanceState.getString("imgPath");

            Log.d("resume", "activityyyyyyyyyyyyyyyyyyy " + imagePath + " !!");

            if(imagePath != null){
                imgFile = new File(imagePath);
                selectedImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                image_preview.setImageBitmap(selectedImage);
                image_preview.setVisibility(View.VISIBLE);
            }

            Log.d("resume", "activityyyyyyyyyyyyyyyyyyy " + String.valueOf(tempImgFile) + " !!");

            Log.d("resume", "activityyyyyyyyyyyyyyyyyyy");

        }

        Log.d("title", "enter create");
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putParcelable("imgFile", tempImgFile);
        outState.putString("imgPath", imagePath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    /* Select category from category activity */
    public void selectCategory(View view) {
        Intent intent = new Intent(NewComplaintActivity.this, CategoryActivity.class);
        startActivityForResult(intent, 1001);
    }

    /* Select camera or image from gallery */
    public void selectImage(View view) {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NewComplaintActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(NewComplaintActivity.this);
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

    private Bitmap getResizedBitmap(Bitmap bitmap, int newWidth, int newHeight){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);
    }

    private byte[] getStringImage(Bitmap bmp){
        Log.d("bmp----------------", bmp.toString() + "");
        String encodedImage;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, output);
        byte[] imageBytes = output.toByteArray();
        encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return imageBytes;
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    private void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_IMAGE_REQUEST);
    }

    private void cameraIntent(){
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
//
//        String imageFileName = timeStamp + ".jpg";
//        File storageDir = Environment.getExternalStoragePublicDirectory(
//                Environment.DIRECTORY_PICTURES + IMAGE_DIRECTORY);
//        imagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        tempImgFile = Uri.fromFile(storageDir);
//        Log.d("resume", String.valueOf(tempImgFile));
//        File file = createImageFile();
//
//        if(Build.VERSION.SDK_INT  < 20)
//            outputFileUri = Uri.fromFile(file);
//        else
//            outputFileUri = FileProvider.getUriForFile(NewComplaintActivity.this,
//                "kuchingitsolution.betterpepperboard.provider", file);
//
//        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
//            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        } else {
//            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            for (ResolveInfo resolveInfo : resInfoList) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                grantUriPermission(packageName, outputFileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            }
//        }
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        startActivityForResult(intent, REQUEST_CAMERA);

        Intent intent;
        if(Build.VERSION.SDK_INT  < 24) {
            intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = Uri.fromFile(getOutputMediaFile());
        } else {
            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            tempImgFile = FileProvider.getUriForFile(NewComplaintActivity.this, "kuchingitsolution.betterpepperboard.provider", getOutputMediaFile());
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgFile);
        startActivityForResult(intent, REQUEST_CAMERA);

//        File file = createImageFile();
//        if(file != null){
//            tempImgFile = Uri.fromFile(createImageFile());
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgFile);
//            startActivityForResult(intent, REQUEST_CAMERA);
//        }

    }

    private File createImageFile() {

        long timeStamp = System.currentTimeMillis();
        String imageFileName = "NAME_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + IMAGE_DIRECTORY);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_CODE && resultCode == RESULT_OK && data != null) {
            Place place = PlacePicker.getPlace(this, data);

            userLocation = String.format("%s", place.getName());
            String name = String.format("%s", place.getName());
            selectedLatitute = String.valueOf(place.getLatLng().latitude);
            selectedLongitute = String.valueOf(place.getLatLng().longitude);

            String locations = String.format("at: %s", name);
            location.setText(locations);
            location.setVisibility(View.VISIBLE);
            getGoogleStaticMap(selectedLatitute, selectedLongitute);
        }

        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
//                onCaptureImageResult(data);
            image_preview.setImageURI(tempImgFile);
            image_preview.setVisibility(View.VISIBLE);
            imagePath = tempImgFile.getPath();
            Log.d("TAG", "File Saved::--->" + tempImgFile.getPath());
            if(Build.VERSION.SDK_INT> 23) {
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImgFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
                selectedImage = BitmapFactory.decodeFile(imagePath);
            imagePath = imageCompressionUtils.saveImage(selectedImage, this); /* change the image path to modified image */
            Log.d("TAG", "File Saved::--->" + imagePath);
            imgFile = new File(imagePath);
        }

        Log.d("request:", requestCode + "result code: " + resultCode);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            Log.d("result:","result code: " + uri.getLastPathSegment());
            String path = imageCompressionUtils.compressImage(uri);
            try {
                Uri imageUri = data.getData();
                imgFile = new File(path);
                InputStream imageStream = getContentResolver().openInputStream(imageUri);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, selectedImage.getWidth() / 2, selectedImage.getHeight() / 2);
                image_preview.setImageBitmap(selectedImage);
                image_preview.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                Log.d("bitmapE", String.valueOf(bitmap));
                e.printStackTrace();
            }
        }

        if(requestCode == 1001 && resultCode == RESULT_OK){
            category.setText(data.getStringExtra(CategoryActivity.CATEGORY));
            categoryID = data.getStringExtra("category_id");
            Toast.makeText(NewComplaintActivity.this, "Selected id: " + data.getStringExtra("category_id"), Toast.LENGTH_SHORT).show();
        }
    }

    private String getStaticMap(String lat, String lon){
        return "http://maps.google.com/maps/api/staticmap?center="
                + lat + "," + lon+"&markers=icon:http://tinyurl.com/2ftvtt6%7C"+ lat +"," + lon
                +"&zoom=16&size=400x400&sensor=false";
    }

    private void getGoogleStaticMap(String latitute, String longitute){

        Picasso.with(getApplicationContext()).cancelRequest(locationPreview);
        Picasso.with(getApplicationContext())
                .load(getStaticMap(latitute, longitute))
                .into(locationPreview);

        locationPreview.setVisibility(View.VISIBLE);

    }

    private void onCaptureImageResult(Intent data) {

        if(data.getExtras() != null){
            selectedImage = (Bitmap) data.getExtras().get("data");
            image_preview.setImageBitmap(selectedImage);
            image_preview.setVisibility(View.VISIBLE);
            imagePath = imageCompressionUtils.saveImage(selectedImage, this);
        }
//        imgFile = new File(imagePath);
//        if(data != null) {
//            selectedImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            tempImgFile = data.getData();
//            try {
//                selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tempImgFile);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            selectedImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
//            imagePath = imageCompressionUtils.saveImage(selectedImage, this);
//            image_preview.setImageBitmap(selectedImage);
//            image_preview.setVisibility(View.VISIBLE);
//            imgFile = new File(imagePath);
//            selectedImage = getResizedBitmap(selectedImage, selectedImage.getWidth() / 2, selectedImage.getHeight() / 2);
//        }
    }

    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    public void selectLocation(View view) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = builder.build(NewComplaintActivity.this);
            startActivityForResult(intent, PLACE_PICKER_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("select", item.getItemId() + " ");
        switch (item.getItemId()) {
            // action with ID action_settings was selected
            case R.id.submit:
                Toast.makeText(this, "Submit selected", Toast.LENGTH_SHORT)
                        .show();
                validate_details();
                break;
            default:
                super.onBackPressed();
                break;
        }
        return true;
    }

    private Boolean validate_details(){

        title = edttitle.getText().toString().trim();
        desc = edtdesc.getText().toString().trim();
        suggestion = edtSuggestion.getText().toString().trim();

        boolean status = true;

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || selectedImage == null || userLocation.equals("") || categoryID == null){
            status = false;
            showMessage("Make sure you have input all the field ");
        } else{
//            imagePath = getStringImage(selectedImage);
            Log.d("imagepath", suggestion + " ");
            if(TextUtils.isEmpty(suggestion))
                suggestion = "null";
            uploadImage(getStringImage(selectedImage));
            loading.setVisibility(View.VISIBLE);
        }
            return status;
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewComplaintActivity.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void uploadImage(final byte[] image){
        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... strings) {

                MultipartBody body = RequestBuilder.uploadNewComplaint(
                        title,
                        categoryID,
                        desc, suggestion,
                        selectedLatitute, selectedLongitute, userLocation,
                        imgFile, session.getUserID());

                CountingRequestBody monitoring = new CountingRequestBody(body, new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(long bytesWritten, long contentLength) {

                        float percentage = 100f * bytesWritten / contentLength;
                        if (percentage >= 0) {
                            loading.setProgress((int)percentage);
                            Log.d("upload", String.valueOf(percentage));
                        }
                    }
                });

                try {
                    return ApiCall.POST(client, Config.NEW_COMPLAINT_URL, monitoring);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
//                Log.d("Result", s);
                loading.setVisibility(View.GONE);
                if(s.equals("Success")) {
                    Toast.makeText(NewComplaintActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewComplaintActivity.this, MainActivity2.class);
                    startActivity(intent);
                    finish();
                } else showMessage(s);
            }
        }.execute();
    }
}