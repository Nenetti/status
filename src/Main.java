
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JFrame;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import ros.NodeHandle;
import ros.ServiceServer;
import ros.Subscriber;
import ros.UserProperty;
import std_msgs.Int32;

public class Main extends NodeHandle {


    private final static int width = 1280;
    private final static int height = 720;

    private String imgPath;
    private ImageView sound_ImageView;
    private ImageView robot_img;

    private Label main_label;
    private Label sub_label;
    private Image speaker_on;
    private Image speaker_off;
    private Image mic_on;
    private Image mic_off;
    private Image duck_normal;
    private Image duck_move;
    private Image duck_success;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("status");
    }

    @Override
    public void start() {
        createWindow(UserProperty.getKey("status.fxml.dir"));

        Subscriber main_subscriber = new Subscriber("/rione/status/main_text", std_msgs.String._TYPE);
        Subscriber sub_subscriber = new Subscriber("/rione/status/sub_text", std_msgs.String._TYPE);
        Subscriber status_subscriber = new Subscriber("/rione/status/status", std_msgs.Int32._TYPE);
        Subscriber speaker_subscriber = new Subscriber("/rione/status/speaker", std_msgs.String._TYPE);
        Subscriber mic_subscriber = new Subscriber("/rione/status/mic", std_msgs.String._TYPE);
        status_subscriber.addMessageListener((message)->{
            int statusID=((std_msgs.Int32)message).getData();
            switch (statusID) {
                case 0:        //PENDING:		未処理(待機)
                    Platform.runLater(() -> {
                        robot_img.setImage(duck_normal);
                    });
                    break;
                case 1:        //ACTIVE:		処理中(移動中)
                    Platform.runLater(() -> {
                        robot_img.setImage(duck_move);
                    });
                    break;
                case 3:        //SUCCEEDED:	完了(待機)
                    Platform.runLater(() -> {
                        robot_img.setImage(duck_success);
                    });
                    break;

                case 2:        //PREEMPTED:	キャンセル受信
                case 4:        //ABORTED:		不正中断
                case 5:        //REJECTED:		目標に到達不可
                case 6:        //PREEMPTING:	キャンセル受信
                case 7:        //RECALLING:	実行前にキャンセル
                case 8:        //RECALLED:		実行前にキャンセル
                case 9:        //LOST:			amcl停止？
            }
        });
        main_subscriber.addMessageListener((message) -> {
            String data = ((std_msgs.String) message).getData().replaceAll(",", "");
            Platform.runLater(() -> {
                main_label.setText(data);
            });
        });
        sub_subscriber.addMessageListener((message) -> {
            String data = ((std_msgs.String) message).getData().replaceAll(",", "");
            Platform.runLater(() -> {
                sub_label.setText(data);
            });
        });
        speaker_subscriber.addMessageListener((message) -> {
            switch (((std_msgs.String) message).getData()) {
                case "ON":
                    Platform.runLater(() -> {
                        sound_ImageView.setImage(speaker_on);
                    });
                    break;
                case "OFF":
                    Platform.runLater(() -> {
                        sound_ImageView.setImage(speaker_off);
                    });
                    break;
            }
        });
        mic_subscriber.addMessageListener((message) -> {
            switch (((std_msgs.String) message).getData()) {
                case "ON":
                    Platform.runLater(() -> {
                        sound_ImageView.setImage(mic_on);
                    });
                    break;
                case "OFF":
                    Platform.runLater(() -> {
                        sound_ImageView.setImage(mic_off);
                    });
                    break;
            }
        });
    }

    public void load() {
        imgPath = UserProperty.getKey("ros.image.dir");
        speaker_on = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("speaker.on"));
        speaker_off = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("speaker.off"));
        mic_on = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("mic.on"));
        mic_off = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("mic.off"));
        duck_normal=NodeFX.toImage(imgPath + "/" + UserProperty.getKey("duck.normal"));
        duck_move=NodeFX.toImage(imgPath + "/" + UserProperty.getKey("duck.move"));
        duck_success=NodeFX.toImage(imgPath + "/" + UserProperty.getKey("duck.success"));
    }

    /********************
     *
     */
    public void createWindow(String fxml) {
        JFrame frame = new JFrame();
        JFXPanel panel = new JFXPanel();
        load();
        NodeFX nodeFX = new NodeFX(fxml);
        Scene scene = new Scene(nodeFX.getRoot(), width, height);
        panel.setScene(scene);
        frame.add(panel);
        frame.setSize(width, height);
        frame.setLocation(0, 0);
        frame.setVisible(true);

        main_label = nodeFX.getLabel("Main_Label");
        sub_label = nodeFX.getLabel("Sub_Label");
        sound_ImageView = nodeFX.getImageView("Sound_Image");
        robot_img = nodeFX.getImageView("Robot_Image");
        Platform.runLater(() -> {
            sound_ImageView.setImage(mic_on);
            robot_img.setImage(duck_normal);
        });
    }
}
