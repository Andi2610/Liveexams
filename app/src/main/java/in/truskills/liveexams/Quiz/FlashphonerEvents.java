package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.bean.Data;
import com.flashphoner.fpwcsapi.bean.StreamStatus;
import com.flashphoner.fpwcsapi.constraints.AudioConstraints;
import com.flashphoner.fpwcsapi.constraints.Constraints;
import com.flashphoner.fpwcsapi.constraints.VideoConstraints;
import com.flashphoner.fpwcsapi.session.Session;
import com.flashphoner.fpwcsapi.session.SessionEvent;
import com.flashphoner.fpwcsapi.session.SessionOptions;
import com.flashphoner.fpwcsapi.session.Stream;
import com.flashphoner.fpwcsapi.session.StreamOptions;
import com.flashphoner.fpwcsapi.session.StreamStatusEvent;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

/**
 * Created by rajiv on 2/13/2017.
 */

interface socketFromStudent {
    void startedStreaming(String teacherId, String studentId);

    void stoppedStreaming();
}

public class FlashphonerEvents implements socketFromTeacher {
    socketFromStudent socketfromstudent;
    Context context;
    public static final String FLASHPHONER = "flashphoner";
    public String studentId, teacherId;
    private final int cameraID = 1;
    private VideoConstraints studentVideoConstraints;
    //to handle streaming events
    private StreamStatusEvent studentEvent = new StreamStatusEvent() {
        @Override
        public void onStreamStatus(Stream stream, StreamStatus streamStatus) {
            Log.d("fp stream status", streamStatus.toString());
            switch (streamStatus.toString()) {
                case "PUBLISHING":
                    Log.d(FLASHPHONER, "published");
                    socketfromstudent.startedStreaming(teacherId, studentId);
                    break;
                case "UNPUBLISHED":
                    socketfromstudent.stoppedStreaming();
                    Log.d(FLASHPHONER, "unpublished");
                    break;
                case "FAILED":
                    Log.d(FLASHPHONER, "failed");
                    break;
            }
        }
    };
    private Constraints studentConstraints;
    private StreamOptions studentOptions;
    private SurfaceViewRenderer extraRender;
    private Session session;
    private SessionOptions sessionOptions;
    private Stream studentStream;

    //in/truskills/liveexams/Quiz/QuizMainActivity.java:140
    public FlashphonerEvents(QuizMainActivity context, SurfaceViewRenderer extraRender) {
        this.extraRender = extraRender;
        this.context = context;
        socketfromstudent = (socketFromStudent) context;
        Flashphoner.init(context);
    }

    //called from in/truskills/liveexams/Quiz/QuizMainActivity.java:953
    @Override
    public void startStreaming(String studentId, String teacherId) {
        Log.d(FLASHPHONER, "startSreaming called from QuizMainActivity");
        this.studentId = studentId;
        this.teacherId = teacherId;

        sessionOptions = new SessionOptions("ws://truteach.com:8080");

        sessionOptions.setLocalRenderer(extraRender);

        /**
         * Session for connection to WCS server is created with method createSession().
         */
        session = Flashphoner.createSession(sessionOptions);

        /**
         * Callback functions for session status events are added to make appropriate changes in controls of the interface when connection is established and closed.
         */

        session.on(new SessionEvent() {
            @Override
            public void onAppData(Data data) {

            }

            @Override
            public void onConnected(final Connection connection) {
            }

            @Override
            public void onRegistered(Connection connection) {

            }

            @Override
            public void onDisconnection(final Connection connection) {

            }
        });

        session.connect(new Connection());

        /*
        * public Constraints(boolean hasAudio,
                   boolean hasVideo)
        * */

        //studentConstraints = new Constraints(true, true);

        studentVideoConstraints = new VideoConstraints();
       // studentVideoConstraints.setCameraId(cameraID);
        studentVideoConstraints.setVideoFps(30);
        studentVideoConstraints.setResolution(1770, 1000);
        studentConstraints = new Constraints(new AudioConstraints(), studentVideoConstraints);

        studentOptions = new StreamOptions(studentId + teacherId);
        studentOptions.setRenderer(extraRender);
        studentOptions.setName(studentId + teacherId);
        studentOptions.setConstraints(studentConstraints);

        extraRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        extraRender.setMirror(false);
        extraRender.requestLayout();

        studentStream = session.createStream(studentOptions);
        studentStream.on(studentEvent);

        studentStream.publish();
    }

    //in/truskills/liveexams/Quiz/QuizMainActivity.java:970
    @Override
    public void stopStreaming() {
        studentStream.stop();
    }

    //in/truskills/liveexams/Quiz/QuizMainActivity.java:929
    @Override
    public void disconnectSession() {
        if (session != null) {
            session.disconnect();
        }
    }
}
