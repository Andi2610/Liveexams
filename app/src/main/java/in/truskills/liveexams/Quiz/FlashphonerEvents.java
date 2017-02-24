package in.truskills.liveexams.Quiz;

import android.content.Context;
import android.util.Log;

import com.flashphoner.fpwcsapi.Flashphoner;
import com.flashphoner.fpwcsapi.bean.Connection;
import com.flashphoner.fpwcsapi.bean.Data;
import com.flashphoner.fpwcsapi.bean.StreamStatus;
import com.flashphoner.fpwcsapi.constraints.Constraints;
import com.flashphoner.fpwcsapi.constraints.VideoConstraints;
import com.flashphoner.fpwcsapi.layout.PercentFrameLayout;
import com.flashphoner.fpwcsapi.session.Session;
import com.flashphoner.fpwcsapi.session.SessionEvent;
import com.flashphoner.fpwcsapi.session.SessionOptions;
import com.flashphoner.fpwcsapi.session.Stream;
import com.flashphoner.fpwcsapi.session.StreamOptions;
import com.flashphoner.fpwcsapi.session.StreamStatusEvent;

import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import in.truskills.liveexams.Miscellaneous.ConstantsDefined;

/**
 * Created by rajiv on 2/13/2017.
 */

interface socketFromStudent {
    void startedStreaming(String teacherId, String studentId);

    void stoppedStreaming();
}

public class FlashphonerEvents implements socketFromTeacher {
    private final PercentFrameLayout parentRender;
    socketFromStudent socketfromstudent;
    Context context;
    public String studentId, teacherId;
    private final int cameraID = 1;
    private VideoConstraints studentVideoConstraints;
    //to handle streaming events
    private StreamStatusEvent studentEvent = new StreamStatusEvent() {
        @Override
        public void onStreamStatus(Stream stream, StreamStatus streamStatus) {
            Log.d(ConstantsDefined.FLASHPHONER, "on StreamStatus");
            switch (streamStatus.toString()) {
                case "PUBLISHING":
                    try {
                        Log.d(ConstantsDefined.FLASHPHONER, "published");
                        socketfromstudent.startedStreaming(teacherId, studentId);
                    }catch(Exception e){
                        Log.d(ConstantsDefined.FLASHPHONER,e.toString());
                    }
                    break;
                case "UNPUBLISHED":
                    socketfromstudent.stoppedStreaming();
                    Log.d(ConstantsDefined.FLASHPHONER, "unpublished");
                    break;
                case "FAILED":
                    Log.d(ConstantsDefined.FLASHPHONER, "failed");
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
    public FlashphonerEvents(QuizMainActivity context, SurfaceViewRenderer extraRender,PercentFrameLayout parentRender) {
        Log.d(ConstantsDefined.FLASHPHONER, "constructor called");
        this.extraRender = extraRender;
        this.context = context;
        this.parentRender = parentRender;
        socketfromstudent = (socketFromStudent) context;
        Log.d(ConstantsDefined.FLASHPHONER, "initialization done");
        Flashphoner.init(context);
    }

    //called from in/truskills/liveexams/Quiz/QuizMainActivity.java:953
    @Override
    public void startStreaming(String studentId, String teacherId) {
        Log.d(ConstantsDefined.FLASHPHONER, "startSreaming called from QuizMainActivity");
        this.studentId = studentId;
        this.teacherId = teacherId;

        sessionOptions = new SessionOptions("ws://truteach.com:8080");
        sessionOptions.setLocalRenderer(extraRender);

        session = Flashphoner.createSession(sessionOptions);
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

        try {
            Log.d(ConstantsDefined.FLASHPHONER, "extra render started"+ extraRender.getId());

            parentRender.setPosition(0, 0, 100, 100);
            extraRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            extraRender.setMirror(false);
            extraRender.requestLayout();
            Log.d(ConstantsDefined.FLASHPHONER, "extra render set");

        } catch (Exception e) {
            Log.d(ConstantsDefined.FLASHPHONER, "exception thrown from extraRender setting " + e.toString());
        }

        try {
            Log.d(ConstantsDefined.FLASHPHONER, "student constraint started");

            studentVideoConstraints = new VideoConstraints();
            studentVideoConstraints.setVideoFps(30);
            studentVideoConstraints.setResolution(1770, 1000);
            studentConstraints = new Constraints(true,true);

            studentOptions = new StreamOptions(studentId);
            studentOptions.setRenderer(extraRender);
            String streamId=studentId;
            studentOptions.setName(streamId);
            Log.d(ConstantsDefined.FLASHPHONER,streamId);
            studentOptions.setConstraints(studentConstraints);

            Log.d(ConstantsDefined.FLASHPHONER, "student constraint set");

        } catch (Exception e) {
            Log.d(ConstantsDefined.FLASHPHONER, "exception thrown from student options setting " + e.toString());
        }

        try {
            Log.d(ConstantsDefined.FLASHPHONER, "student streaming setup");

            studentStream = session.createStream(studentOptions);
            Log.d(ConstantsDefined.FLASHPHONER, "student create stream");

            studentStream.on(studentEvent);
            Log.d(ConstantsDefined.FLASHPHONER, "student create events");

            studentStream.publish();
            Log.d(ConstantsDefined.FLASHPHONER, "published");

        } catch (Exception e) {
            Log.d(ConstantsDefined.FLASHPHONER, "exception thrown from studentStream setting " + e.toString());
        }

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
