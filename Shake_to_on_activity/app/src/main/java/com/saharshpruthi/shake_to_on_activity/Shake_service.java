package com.saharshpruthi.shake_to_on_activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

public class Shake_service extends Service
{
    public Shake_service() {
    }



    private SensorManager sensorManager;
    private Sensor aSensor;
    boolean acc_disp = false;
    private long lastUpdate;
    String msg = "Service : ";


    double zdotdot=0.0;

    double zdotdot_old= 0.0;
    int flag_acc_achived=0,flag_nve_acc_achived=0,flag_nve_nve_acc_achived=0;


    double ACC_TO_BE_ACHIVED =10.0;
    double MIN_TIME =1.5;



    long time, time_old;//in nannos
    long flag_time =0, camera_start_time_flag=0;
    String  activated_status= "NO";

    double time_diff;//in sec



    @Override
    public void onCreate()
    {
        super.onCreate();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        aSensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);




        if (aSensor != null) {




            sensorManager.registerListener(mySensorEventListener, aSensor,
                    SensorManager.SENSOR_DELAY_FASTEST);// worked with SENSOR_DELAY_FASTEST                 SENSOR_DELAY_NORMAL




            Log.i("Acellromerter Service", "Registerered for  Sensor");
        } else {
            Log.e("Acellromerter Service", " not Registerered for Sensor");
            Toast.makeText(this, "ORIENTATION Sensor not found", Toast.LENGTH_LONG).show();
//            finish();
        }








        Toast.makeText(this," Shake Service Created",Toast.LENGTH_LONG).show();
    }








    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this," Shake Service Started",Toast.LENGTH_LONG).show();


        int ONGOING_NOTIFICATION_ID = 5;


        Intent notificationIntent = new Intent(this, Shake_service.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.notification_title))
                .setContentText(getText(R.string.notification_message))
//                .setSmallIcon(R.drawable.mouse)
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.ticker_text))
                .build();

        startForeground(ONGOING_NOTIFICATION_ID, notification);



        return super.onStartCommand(intent, flags, startId);
    }






    private SensorEventListener mySensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                getAccelerometer(event);
            }
        }
    };

    //13096719 ,9450261,9733385



    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;


//        Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


//            time_old = time;
            time = SystemClock.elapsedRealtimeNanos();


            time_diff = (time-flag_time)*(0.000000001);

            zdotdot_old = zdotdot ;
            zdotdot = values[2];

            if(zdotdot> ACC_TO_BE_ACHIVED && flag_acc_achived==0)// Initial shake
            {
                flag_acc_achived=1;
                flag_time = time;

            }
            else if(zdotdot<-23.0)
            {

            }


            if(time_diff>MIN_TIME)
            {
                time_diff=0;
                flag_acc_achived=0;
                flag_nve_acc_achived=0;
                flag_nve_nve_acc_achived=0;

            }


            if( flag_acc_achived==1 && zdotdot< (-1*ACC_TO_BE_ACHIVED) && time_diff<MIN_TIME )
            {
                flag_nve_acc_achived = 1;
                flag_time = time;
            }


            if(flag_acc_achived==1 && flag_nve_acc_achived == 1 && zdotdot> ACC_TO_BE_ACHIVED  && time_diff<MIN_TIME )
            {
                flag_nve_nve_acc_achived =1;
                activated_status = "Yes";

                camera_start_time_flag = time;



//                mVibrator.vibrate(300);

                Intent intent_ACC = new Intent(Shake_service.this, CameraRecorder.class);
                intent_ACC.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent_ACC);



//                Intent intent = new Intent(Shake_service.this, RecorderService.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startService(intent);
//

            }



        if(activated_status == "Yes" ) {
            if ((time - camera_start_time_flag) * (0.000000001) > 4) {


                camera_start_time_flag = time;


                Intent intent = new Intent(Shake_service.this, RecorderService.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(intent);




            }
//            if(flag_nve_nve_acc_achived==1 && flag_nve_acc_achived==1 && flag_acc_achived ==1)
//            {
//                activated_status = "Yes";
//            }
//
        }



            if(activated_status == "Yes" ) {
                if ((time - camera_start_time_flag) * (0.000000001) > 15) {


                    stopService(new Intent(Shake_service.this, RecorderService.class));



                }
//            if(flag_nve_nve_acc_achived==1 && flag_nve_acc_achived==1 && flag_acc_achived ==1)
//            {
//                activated_status = "Yes";
//            }
//
            }




            Log.d(msg, "zdotdot=" + zdotdot);
            Log.d(msg, "System time" + time);
            Log.d(msg, "ACTIVATED" + activated_status );

            Log.d(msg, "-----------------------" );





    }





    @Override
    public void onDestroy() {
        super.onDestroy();

        if (aSensor != null) {
            sensorManager.unregisterListener(mySensorEventListener);
        }


        Toast.makeText(this," Shake Service Destroyed",Toast.LENGTH_LONG).show();

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
