package org.ligi.rcmon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ligi.android.io.ReflectingBluetoothCommunicationaAdapter;
import org.ligi.ufo.MKCommunicator;
import org.ligi.ufo.VesselData;

public class MainActivity extends Activity {

    private MKCommunicator mk;
    private Handler handler;
    private TextView batt_tv;
    private StickView stickView;

    private ProgressBar poti1Progress;
    private ProgressBar poti2Progress;
    private ProgressBar poti3Progress;

    private TextView poti1Text;
    private TextView poti2Text;
    private TextView poti3Text;

    private ReflectingBluetoothCommunicationaAdapter comm_adapter;

    private final static String mac = "00:11:12:06:01:78";

    private int last_bytes_in;
    private long last_new_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            | WindowManager.LayoutParams.FLAG_FULLSCREEN
            | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        stickView=(StickView)findViewById(R.id.stickView);

        poti1Progress=(ProgressBar)findViewById(R.id.poti1Progress);
        poti2Progress=(ProgressBar)findViewById(R.id.poti2Progress);
        poti3Progress=(ProgressBar)findViewById(R.id.poti3Progress);

        poti1Text=(TextView)findViewById(R.id.poti1Text);
        poti2Text=(TextView)findViewById(R.id.poti2Text);
        poti3Text=(TextView)findViewById(R.id.poti3Text);

        switchBtOnIfOff();

        mk=new MKCommunicator();

        comm_adapter = new ReflectingBluetoothCommunicationaAdapter(mac);
        mk.setCommunicationAdapter(comm_adapter);

        mk.connect_to("btspp://"+mac,mac);

        handler=new Handler();

        batt_tv=(TextView)findViewById(R.id.battTV);

        handler.post(new UpdateRunnable());

        mk.switch_to_fc();
    }

    private class UpdateRunnable implements Runnable {

        @Override
        public void run() {
            mk.user_intent=MKCommunicator.USER_INTENT_RCDATA;
            String res="";

            for (byte i=1;i<8;i++) {
                res+=" " + i+":" + mk.stick_data.getStickValue(i)+"\n";
            }

            res+="nick"+VesselData.attitude.getNick()+"\n";
            res+="roll"+VesselData.attitude.getRoll()+"\n";

            stickView.setRoll(-mk.stick_data.getStickValue((byte)2));
            stickView.setGas(mk.stick_data.getStickValue((byte)3));
            stickView.setYaw(-mk.stick_data.getStickValue((byte)4));
            stickView.setNick(-mk.stick_data.getStickValue((byte) 5));

            setProgressBarPotiProps(poti1Progress,poti1Text,(byte)1);

            setProgressBarPotiProps(poti2Progress,poti2Text, (byte) 6);
            setProgressBarPotiProps(poti3Progress,poti3Text,(byte)7);


            batt_tv.setText(res + " - " +mk.stats.stick_data_count + "/" + mk.stats.stick_data_request_count + " " +
            mk.stats.bytes_in + " " + mk.user_intent + " " + MKCommunicator.USER_INTENT_RCDATA

            +"\n"+ VesselData.attitude.getNick()  + " " + VesselData.battery.getVoltage()+"V" + " " + mk.isConnected());


            if (last_bytes_in<mk.stats.bytes_in) {
                last_bytes_in=mk.stats.bytes_in;
                last_new_data=System.currentTimeMillis();
            }

            if ((System.currentTimeMillis()-last_new_data)>1000) {
                comm_adapter.disconnect();
                comm_adapter.connect();
                mk.setCommunicationAdapter(comm_adapter);

                mk.stats.reset();
                last_bytes_in=0;
                mk.connect_to("btspp://"+mac,mac);
                last_new_data=System.currentTimeMillis()+1000;
                handler.postDelayed(this, 1000);
            } else {
                handler.postDelayed(this, 20);
            }
        }
    }

    private void setProgressBarPotiProps(ProgressBar pb,TextView tv,byte pos) {
        pb.setMax(400); //+/- 200
        int val = mk.stick_data.getStickValue(pos) + 200;
        tv.setText(""+val);
        pb.setProgress(val);
    }



    private void switchBtOnIfOff() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
        }
    }


}
