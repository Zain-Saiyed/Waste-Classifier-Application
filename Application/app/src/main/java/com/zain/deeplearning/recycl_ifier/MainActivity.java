package com.zain.deeplearning.recycl_ifier;

import android.app.Activity;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends Activity
{
    private static final int CAMERA_REQUEST = 1888;

    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    public static final int RESULTS_TO_SHOW =3;
    public static final int IMAGE_MEAN = 128;
    public static final float IMAGE_STD = 128.0f;

    private final Interpreter.Options tfliteOptions = new Interpreter.Options();
    private Interpreter tflite;
    int[] intValues;


    Button photoButton;
    TextView tv_result,tx1,tx2;
    ByteBuffer imgData = null;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.imageView = (ImageView)this.findViewById(R.id.imageView1);
        photoButton = (Button) this.findViewById(R.id.btn_classify);
        tv_result = this.findViewById(R.id.tv_result);

//        load_model();

        photoButton.setOnClickListener(new View.OnClickListener()
        {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v)
            {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    protected void onActivityResult(int requestCode, int resultCode ,Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            // Get image and store it in BitMap
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            int width = photo.getWidth();
            int height = photo.getHeight();
            tv_result.setText(width+"   "+height);

            imageView.setImageBitmap(photo);


//            imgData.rewind();
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(photo, 600, 600, false);

            imageView.setImageBitmap(Bitmap.createScaledBitmap(resizedBitmap, 800, 800, false));



            int[] pix = new int[224 * 224];
            resizedBitmap.getPixels(pix, 0, 224, 0, 0, 224, 224);

//            int bytes = resizedBitmap .getByteCount();
//            resizedBitmap.copyPixelsToBuffer(imgData);
//            byte[] array = imgData.array();
//
//            Bitmap image = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//            int[] intArray = new int[224 * 224];

//            float prediction = doInference(pix);
//            tv_result.setText(Float.toString(prediction));

            intValues = new int[224 * 224];
            try{
                tflite = new Interpreter(loadModelFile(),tfliteOptions);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            imgData = ByteBuffer.allocateDirect(4*224*224*3);

            imgData.order(ByteOrder.nativeOrder());

            float[][] labelProbArray = new float[1][2];
            Bitmap input_image = getResizedBitmap(photo,224,224);

            convertBitmapToByteBuffer(input_image);

            tflite.run(imgData,labelProbArray);

            tx1 =  this.findViewById(R.id.tv_result_organic);
            tx2 =  this.findViewById(R.id.tv_result_recycle);
            tx1.setText((labelProbArray[0][0]*100)+" %");
            tx2.setText((labelProbArray[0][1]*100)+" %");
            if ( labelProbArray[0][0] > labelProbArray[0][1]  )
                tv_result.setText("ORGANIC!");
            else
                tv_result.setText("RECYCLEABLE!");
        }
    }
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < 224 ; ++i) {
            for (int j = 0; j < 224; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                // if quantized, convert each rgb value to a byte, otherwise to a float

                imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
            }
        }
    }
    public Bitmap getResizedBitmap(Bitmap bm, int nwidth, int nheight){
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) nwidth )/width;
        float scaleHeight = ((float) nheight )/height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bm,0,0,width, height,matrix,false);

        return resizedBitmap;

    }
    private MappedByteBuffer loadModelFile() throws IOException {

        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();

        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

//    public void load_model(){
//
//        try{
//            tflite = new Interpreter(loadModelFile(MainActivity.this,"model.tflite"));
//        }
//            catch(Exception e){
//            e.printStackTrace();
//        }
//        imgData = ByteBuffer.allocateDirect(224 * 224 * 3 * 4);
//        imgData.order(ByteOrder.nativeOrder());
//
//    }
//    public float[] predict(float[] data) {
//        //array to store the result/output of the model
//        float[] result = new float[2];
//        //feed the input to our model, passing name of the input node, data, and size of the input
//        inferenceInterface.feed("x", data, 224);
//        //run the model on the input data, passing the name of output nodes
//        inferenceInterface.run({"y"});
//        //get the predicted result from the model, passing name of the output node, and the array to store the result
//        inferenceInterface.fetch(OUTPUT_NODE, result);
//        //return the result
//        return result;
//    }
//
//
//
//
//    public float doInference(int[] image){
//
//        float[] inputVal = new float[1];
//        float[] outputVal = new float[2];
////        inputVal[0] = Float.valueOf()
//
//        tflite.run(image,outputVal);
//
//        float inferredValue = outputVal[0];
//
//        return inferredValue;
//
//    }


}

