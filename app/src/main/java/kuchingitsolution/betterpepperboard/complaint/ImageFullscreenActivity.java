package kuchingitsolution.betterpepperboard.complaint;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import kuchingitsolution.betterpepperboard.R;
import uk.co.senab.photoview.PhotoView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class ImageFullscreenActivity extends AppCompatActivity {

    private PhotoView imageView;
    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_fullscreen);

        imageView = findViewById(R.id.fullscreen_image);
        loading = findViewById(R.id.loading_img);

        if (getIntent() != null) {
            String url = getIntent().getStringExtra("image");
            Picasso.with(this).load(url).into(imageView, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    loading.setVisibility(View.GONE);
                }

                @Override
                public void onError() {

                }
            });
            /*Glide.with(ImageFullscreenActivity.this)
                    .load(url)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            loading.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(imageView);*/
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
