package com.moyo.carzrideon.Activitites;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.moyo.carzrideon.R;
import com.moyo.carzrideon.Volley.VolleyMultiPartRequest;
import com.moyo.carzrideon.Constants.Constants;
import com.moyo.carzrideon.Fragments.BottomSheetFragment_ChooseImage;

import com.moyo.carzrideon.Views.CustomProgressDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ContactUsActivity extends AppCompatActivity {

    private static final String SERVER_URL = Constants.BASE_URL+"contactus";
    private EditText type, message;
    private TextView attachmentText, attachment;
    private Uri fileUri;
    public static int MEDIA_TYPE_IMAGE = 22;
    public static String IMAGE_DIRECTORY_NAME = "Avloff";
    private int PICK_IMAGES = 1;
    final int SELECT_PICTURE = 2, TAKE_PICTURE = 3;
    private Bitmap thumbnail_r;
    private static final int REQUEST_WRITE_STORAGE = 112;
    private String imagePath;
    private byte[] byteArray;
    private Button submit;
    public static final String DEFAULT = "N/A";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().hide();
            ImageView left_arrow = (ImageView) findViewById(R.id.left_arrow);
            left_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        type = (EditText) findViewById(R.id.type);
        message = (EditText) findViewById(R.id.message);
        attachment = (TextView) findViewById(R.id.attachment);
        attachmentText = (TextView) findViewById(R.id.attachmenttext);
        attachmentText.setVisibility(View.GONE);
        submit = (Button) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (message.getText().toString().isEmpty()) {
                    Toast.makeText(ContactUsActivity.this, "Please enter some message", Toast.LENGTH_SHORT).show();
                } else {
                    uploadDocument(byteArray, imagePath);
                }
            }
        });


        type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedbackType();
            }
        });
        attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (attachment.getText().toString().equalsIgnoreCase("cancel attachment")) {
                    attachmentText.setVisibility(View.GONE);
                    imagePath = "";
                    attachment.setText("Add Attachment (Optional)");
                } else {
                    boolean hasPermission = (ContextCompat.checkSelfPermission(ContactUsActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(ContactUsActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                                REQUEST_WRITE_STORAGE);
                    } else {

                        final BottomSheetFragment_ChooseImage myBottomSheet = BottomSheetFragment_ChooseImage.newInstance("Modal Bottom Sheet");
                        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());

                    }
                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void selectFeedbackType() {

        final Dialog dialog = new Dialog(ContactUsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_popup_feedback_type);

        TextView feedback = (TextView) dialog.findViewById(R.id.feedback);
        TextView query = (TextView) dialog.findViewById(R.id.query);
        TextView complaint = (TextView) dialog.findViewById(R.id.complaint);

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setText("Feedback");
                dialog.cancel();
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setText("Query");
                dialog.cancel();
            }
        });

        complaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                type.setText("Complaint");
                dialog.cancel();
            }
        });

        dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;

        int width = displaymetrics.widthPixels;


        dialog.getWindow().setLayout(width, lp.height);
    }

    /**
     *
     */
    public void takePicture() {

        Intent intent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                /*
                 * File photo = new
                 * File(Environment.getExternalStorageDirectory(),
                 * "Pic.jpg"); intent.putExtra(MediaStore.EXTRA_OUTPUT,
                 * Uri.fromFile(photo)); imageUri = Uri.fromFile(photo);
                 */
        // startActivityForResult(intent,TAKE_PICTURE);

        Intent intents = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intents.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intents, TAKE_PICTURE);
    }


    public void selectgImageFromGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                SELECT_PICTURE);

    }

    public String checkNull(String s) {
        if (s != null && !s.isEmpty())
            return s;
        else return "";

    }
  /*  private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            profilepic.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }*/

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                Bitmap thePic = null;
                try {
                    thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);


                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    thePic.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byteArray = stream.toByteArray();

                    imagePath = resultUri.getPath();
                    attachmentText.setVisibility(View.VISIBLE);
                    attachmentText.setText("* File atttached");
                    attachment.setText("Cancel Attachment");


                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }


        } else {
            switch (requestCode) {



         /*   case Crop.REQUEST_CROP:


                    try {
                        Bundle extras = data.getExtras();

                        Bitmap thePic = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(data));

                        FaceCrop faceCrop = new FaceCrop(this);
                        faceCrop.setFaceCropAsync(profilepic, thePic);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        thePic.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                        byte[] byteArray = stream.toByteArray();

                        progressDialog = CustomProgressDialog.show(UserProfileEdit.this);
                        // chooseImage.setImageDrawable(getResources().getDrawable(R.drawable.progressdrawable));
                        uploadDocument(byteArray, Crop.getOutput(data).getPath());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                break;
*/

                case SELECT_PICTURE:
                    if (resultCode == RESULT_OK && data != null && data.getData() != null) {

                        Uri uri = data.getData();

                        try {
                      /*  thumbnail_r = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        // profilepic.setImageBitmap(thumbnail_r);
                        //updateImage(uri);
                        FaceCrop faceCrop = new FaceCrop(this);

                        faceCrop.setFaceCropAsync(profilepic, thumbnail_r);


                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(thumbnail_r, 700, 700,
                                false);

                        // rotated
                        thumbnail_r = imageOreintationValidator(resizedBitmap,
                                uri.getPath());
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        thumbnail_r.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        progressDialog = CustomProgressDialog.show(UserProfileEdit.this);
                        // chooseImage.setImageDrawable(getResources().getDrawable(R.drawable.progressdrawable));
                        uploadDocument(byteArray, uri.getPath());*/
                     /*   croppedImageUri = uri;
                        Intent cropIntent = new Intent("com.android.camera.action.CROP");
                        //indicate image type and Uri
                        cropIntent.setDataAndType(uri, "image*//*");
                        //set crop properties
                        cropIntent.putExtra("crop", "true");
                        //indicate aspect of desired crop
                        cropIntent.putExtra("aspectX", 1);
                        cropIntent.putExtra("aspectY", 1);
                        //indicate output X and Y
                        cropIntent.putExtra("outputX", 256);
                        cropIntent.putExtra("outputY", 256);
                        //retrieve data on return
                        cropIntent.putExtra("return-data", true);
                        //start the activity - we handle returning in onActivityResult
                        startActivityForResult(cropIntent, PIC_CROP);*/
                            beginCrop(data.getData());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;


                case TAKE_PICTURE:
                    if (resultCode == RESULT_OK) {

                        previewCapturedImage();

                    }

                    break;


            }
        }


    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        // Crop.of(source, destination).asSquare().start(this);

        CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(16, 16)
                .setRequestedSize(1080, 1080)
                .start(this);

    }

    @SuppressLint("NewApi")
    private void previewCapturedImage() {
        try {
            // hide video preview

            //image.setVisibility(View.VISIBLE);

            // bimatp factory
           /* BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500,
                    false);

            // rotated
            thumbnail_r = imageOreintationValidator(resizedBitmap,
                    fileUri.getPath());

            //   updateImage(fileUri);


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            thumbnail_r.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            // chooseImage.setImageDrawable(getResources().getDrawable(R.drawable.progressdrawable));
            progressDialog = CustomProgressDialog.show(UserProfileEdit.this);
            uploadDocument(byteArray, fileUri.getPath());

            FaceCrop faceCrop = new FaceCrop(this);
            faceCrop.setFaceCropAsync(profilepic, thumbnail_r);

            // profilepic.setImageBitmap(thumbnail_r);
            //image.setBackground(null);
            //image.setImageBitmap(thumbnail_r);
            // IsImageSet = true;*/
          /*  croppedImageUri = fileUri;
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(fileUri, "image*//*");
            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);*/
            beginCrop(fileUri);


        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    // for roted image......
    private Bitmap imageOreintationValidator(Bitmap bitmap, String path) {

        ExifInterface ei;
        try {
            ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    private Bitmap rotateImage(Bitmap source, float angle) {

        Bitmap bitmap = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            bitmap = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                    source.getHeight(), matrix, true);
        } catch (OutOfMemoryError err) {
            source.recycle();
            Date d = new Date();
            CharSequence s = DateFormat
                    .format("MM-dd-yy-hh-mm-ss", d.getTime());
            String fullPath = Environment.getExternalStorageDirectory()
                    + "/RYB_pic/" + s.toString() + ".jpg";
            if ((fullPath != null) && (new File(fullPath).exists())) {
                new File(fullPath).delete();
            }
            bitmap = null;
            err.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap decodeSampledBitmapFromResource(String pathToFile,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToFile, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        Log.e("inSampleSize", "inSampleSize______________in storage"
                + options.inSampleSize);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathToFile, options);
    }


    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        }

        return inSampleSize;
    }


    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }


    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public void startImageSelection() {
        Intent intent = new Intent();

        intent.setType("image/*");


        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGES);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {


            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //reload my activity with permission granted or use the features what required the permission
                    boolean hasPermission = (ContextCompat.checkSelfPermission(ContactUsActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

                    if (!hasPermission) {
                        ActivityCompat.requestPermissions(ContactUsActivity.this,
                                new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA},
                                REQUEST_WRITE_STORAGE);
                    } else {

                        final BottomSheetFragment_ChooseImage myBottomSheet = BottomSheetFragment_ChooseImage.newInstance("Modal Bottom Sheet");
                        myBottomSheet.show(getSupportFragmentManager(), myBottomSheet.getTag());

                    }

                } else {
                    //   Toast.makeText(myAccountActivity.this, "Please allow us to Take picture from your device", Toast.LENGTH_LONG).show();


                    Toast.makeText(ContactUsActivity.this, "Please allow us to Take picture from your device", Toast.LENGTH_SHORT).show();

                }
            }
            break;


        }

    }


    public void uploadDocument(final byte[] bytes, final String uri) {

        final CustomProgressDialog progressDialog = CustomProgressDialog.show(ContactUsActivity.this);
        RequestQueue requestQueue = Volley.newRequestQueue(ContactUsActivity.this);

        VolleyMultiPartRequest multipartRequest = new VolleyMultiPartRequest(Request.Method.POST, SERVER_URL, new com.android.volley.Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {


                String resultResponse = new String(response.data);

                JSONObject jsonResponse = null;

                try {


                 //   Log.d("response",resultResponse);
                    jsonResponse = new JSONObject(resultResponse);

                    if (jsonResponse != null && jsonResponse.has("error") && !jsonResponse.getString("error").isEmpty() && jsonResponse.getString("error").equalsIgnoreCase("false")) {
                        Toast.makeText(ContactUsActivity.this, "Thanks for the support,we get back to you", Toast.LENGTH_SHORT).show();
                        onBackPressed();

                    } else {
                        Toast.makeText(ContactUsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    }
                    progressDialog.cancel();
                    //   chooseImage.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_menu_camera));

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ContactUsActivity.this, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                    // chooseImage.setImageDrawable(getResources().getDrawable(R.drawable.progressdrawable));
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // chooseImage.setImageDrawable(getResources().getDrawable(R.drawable.progressdrawable));
                Toast.makeText(ContactUsActivity.this, "Unable to connect server", Toast.LENGTH_SHORT).show();
                progressDialog.cancel();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                SharedPreferences sharedPreferences = getSharedPreferences("rideon", Context.MODE_PRIVATE);

                params.put("type", type.getText().toString()+ "");
                params.put("message", message.getText().toString()+ "");
                params.put("Authorization", sharedPreferences.getString("apikey", DEFAULT)+ "");

                return params;
            }

            @Override
            protected Map<String, VolleyMultiPartRequest.DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView

                if(uri!=null && !uri.isEmpty())
                params.put("uploaded_file", new VolleyMultiPartRequest.DataPart(uri, bytes, "image/jpeg"));


                return params;
            }

        };

        requestQueue.add(multipartRequest);
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(50000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


    }
}
