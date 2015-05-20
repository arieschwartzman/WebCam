package com.schwartzman.arie.webcam;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {

    private ImageView mImageView;

    private static final String
            WEB_CAMERA_CAPABILITY_NAME = "web_camera";

    private GoogleApiClient mGoogleApiClient;
    private String nodeId;
    private Button mButton;

    private void setupWebCamera() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient
                            .blockingConnect(1000, TimeUnit.MILLISECONDS);
                    NodeApi.GetConnectedNodesResult result =
                            Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                    List<Node> nodes = result.getNodes();
                    if (nodes.size() > 0) {
                        nodeId = nodes.get(0).getId();
                    }
                    mGoogleApiClient.disconnect();
                }
            }).start();


    }

    private void sendToast() {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mGoogleApiClient.blockingConnect(1000, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, nodeId, "Send me a WebCam image", null);
                    mGoogleApiClient.disconnect();
                }
            }).start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWebCamera();
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);

        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                mImageView = (ImageView) stub.findViewById(R.id.imageView);

                mButton = (Button) stub.findViewById(R.id.button);

                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sendToast();
                    }
                });
            }
        });
    }

}
