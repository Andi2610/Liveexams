package in.truskills.liveexams.MainScreens;


import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import in.truskills.liveexams.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaqFragment extends Fragment {


    WebView webView;

    SlidingDrawer contactUsDrawer;
    Button contactUsButton;
    ImageView contactUsImage;

    TextView tv1,tv2,tv3,tv4;

    Button locate_us;


    public FaqFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_faq, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        webView = (WebView) getActivity().findViewById(R.id.webViewForFaq);
        locate_us = (Button) getActivity().findViewById(R.id.locate_us);

        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setDisplayZoomControls(false);

        tv1=(TextView)getActivity().findViewById(R.id.tv1);
        tv2=(TextView)getActivity().findViewById(R.id.tv2);
        tv3=(TextView)getActivity().findViewById(R.id.tv3);
        tv4=(TextView)getActivity().findViewById(R.id.tv4);

        final Typeface typeface2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Comfortaa-Bold.ttf");

        tv1.setTypeface(typeface2);
        tv2.setTypeface(typeface2);
        tv3.setTypeface(typeface2);
        tv4.setTypeface(typeface2);

        contactUsButton=(Button)getActivity().findViewById(R.id.contactUsButton);

        contactUsImage=(ImageView)getActivity().findViewById(R.id.contactUsHandleImage);

        contactUsDrawer=(SlidingDrawer)getActivity().findViewById(R.id.contactUsDrawer);

        String content="<html><body><img src=\"file:///android_asset/pic_1.png\"/><br><img src=\"file:///android_asset/pic_2.png\"/><br>" +
                "<img src=\"file:///android_asset/pic_3.png\"/><br><img src=\"file:///android_asset/pic_4.png\"/><br>" +
                "<img src=\"file:///android_asset/pic_5.png\"/><br><img src=\"file:///android_asset/pic_6.png\"/><br>" +
                "<img src=\"file:///android_asset/pic_7.png\"/><br></body></html>";

        webView.loadDataWithBaseURL(null, content, "text/HTML", "UTF-8", null);

        contactUsDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {
            @Override
            public void onDrawerOpened() {
                contactUsImage.setImageResource(R.drawable.down_arrow);
            }
        });

        contactUsDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {
            @Override
            public void onDrawerClosed() {
                contactUsImage.setImageResource(R.drawable.up_arrow);

            }
        });

        locate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = 23.0509;
                double longitude = 72.5185;
                String label = "Truskills Solutions";
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude + "(" + label + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }

}
