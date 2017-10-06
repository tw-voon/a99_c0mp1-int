package kuchingitsolution.betterpepperboard.new_complaint;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import static kuchingitsolution.betterpepperboard.R.id.loading;

public class NewComplaintActivity extends AppCompatActivity {

    TextView category, location;
    EditText edttitle, edtdesc;
    ImageView image_preview, locationPreview;
    String selectedLatitute, selectedLongitute, userLocation, userChoosenTask = "", userID, categoryID = null, imagePath;
    String title, desc, imageString;
    final static int PLACE_PICKER_CODE = 1000, REQUEST_CAMERA = 1888, PICK_IMAGE_REQUEST = 2;
    Bitmap selectedImage;
    private OkHttpClient client;
    ProgressDialog loading;
    Session session;
    File imgFile;
    ImageCompressionUtils imageCompressionUtils;

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
        image_preview =  findViewById(R.id.image_preview);
        locationPreview =  findViewById(R.id.location_preview);
        OkHttpClient.Builder b = new OkHttpClient.Builder();
        b.readTimeout(200, TimeUnit.SECONDS);
        b.writeTimeout(300, TimeUnit.SECONDS);
        b.retryOnConnectionFailure(true);
        client = b.build();
        loading = new ProgressDialog(this);
        loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        session = new Session(this);
        imageCompressionUtils = new ImageCompressionUtils(this);

        Log.d("title", "enter create");
//        if(savedInstanceState != null){
//            Log.d("title", "enter create2");
//            category.setText(savedInstanceState.getString(CATEGORY));
//            edttitle.setText(savedInstanceState.getString(TITLE));
//            edtdesc.setText(savedInstanceState.getString(DESC));
//            userChoosenTask = savedInstanceState.getString(CHOSEN_TASK);
//            if((userChoosenTask.equals("Take Photo") || userChoosenTask.equals("Choose from Library")) && userChoosenTask != null){
//                image_preview.setVisibility(View.VISIBLE);
//                Bitmap bitmap = savedInstanceState.getParcelable(CHOSEN_IMAGE);
//                image_preview.setImageBitmap(bitmap);
//            }
//        }
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

    public void selectCategory(View view) {
        Intent intent = new Intent(NewComplaintActivity.this, CategoryActivity.class);
        startActivityForResult(intent, 1001);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

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

    private void galleryIntent(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
        startActivityForResult(Intent.createChooser(intent, "Select File"),PICK_IMAGE_REQUEST);
    }

    private void cameraIntent(){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = timeStamp + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        imagePath = storageDir.getAbsolutePath() + "/" + imageFileName;
        File file = new File(imagePath);
        Uri outputFileUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, REQUEST_CAMERA);
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
            onCaptureImageResult(data);
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
        imgFile = new  File(imagePath);
        if(imgFile.exists()) {
            selectedImage = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            selectedImage = getResizedBitmap(selectedImage, selectedImage.getWidth() / 2, selectedImage.getHeight() / 2);
            image_preview.setImageBitmap(selectedImage);
            image_preview.setVisibility(View.VISIBLE);
        }
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
//                Intent intent = new Intent(MainActivity2.this, SettingActivity.class);
//                startActivity(intent);
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
        boolean status = true;

        if(TextUtils.isEmpty(title) || TextUtils.isEmpty(desc) || selectedImage == null || userLocation.equals("") || categoryID == null){
            status = false;
            showMessage("success");
        } else{
//            imagePath = getStringImage(selectedImage);
//            Log.d("imagepath", imagePath);
            uploadImage(getStringImage(selectedImage));
            loading.show();
        }
            return status;
    }

    private void showMessage(String message){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewComplaintActivity.this);
        alertDialogBuilder.setMessage("Make sure you have input all the field " + message);
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void uploadImage(final byte[] image){
        new AsyncTask<String, Integer, String>(){

            @Override
            protected String doInBackground(String... strings) {

                MultipartBody body = RequestBuilder.uploadNewComplaint(
                        title,
                        categoryID,
                        desc,
                        selectedLatitute, selectedLongitute, userLocation,
                        imgFile, session.getUserID());

//                Log.d("result", image.toString());

                CountingRequestBody monitoring = new CountingRequestBody(body, new CountingRequestBody.Listener() {
                    @Override
                    public void onRequestProgress(long bytesWritten, long contentLength) {

                        float percentage = 100f * bytesWritten / contentLength;
                        if (percentage >= 0) {
                            loading.setProgress((int)percentage);
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
                loading.dismiss();
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