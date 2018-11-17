/*
 * Copyright (C) 2014 ubuntu.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */


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
    private AnchorPane root;
    private ImageView sound_ImageView;
    private ImageView robot_img;

    private Label main_label;
    private Label sub_label;
    private Image speaker_on;
    private Image speaker_off;
    private Image mic_on;
    private Image mic_off;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("status");
    }

    @Override
    public void start() {
        createWindow(UserProperty.getKey("status.fxml.dir"));

        Subscriber main_subscriber = new Subscriber("status/main_text", std_msgs.String._TYPE);
        main_subscriber.addMessageListener((message) -> {
            String data = ((std_msgs.String) message).getData().replaceAll(",", "");
            Platform.runLater(() -> {
                main_label.setText(data);
            });
        });
        Subscriber sub_subscriber = new Subscriber("status/sub_text", std_msgs.String._TYPE);
        sub_subscriber.addMessageListener((message) -> {
            String data = ((std_msgs.String) message).getData().replaceAll(",", "");
            Platform.runLater(() -> {
                sub_label.setText(data);
            });
        });
        Subscriber speaker_subscriber = new Subscriber("status/speaker", std_msgs.String._TYPE);
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
        Subscriber mic_subscriber = new Subscriber("status/mic", std_msgs.String._TYPE);
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
        speaker_on = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("Speaker_ON"));
        speaker_off = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("Speaker_OFF"));
        mic_on = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("Mic_ON"));
        mic_off = NodeFX.toImage(imgPath + "/" + UserProperty.getKey("Mic_OFF"));
    }

    /********************
     *
     */
    public void createWindow(String fxml) {
        JFrame frame = new JFrame();
        JFXPanel panel = new JFXPanel();
        load();
        root = new AnchorPane();
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
        sound_ImageView.setImage(mic_on);
        robot_img = nodeFX.getImageView("Robot_Image");
        /*RotateTransition animation=new RotateTransition();
        animation.setNode(mic_img);
        animation.setDuration(Duration.millis(1000));
        animation.setFromAngle(-30);
        animation.setToAngle(30);
        animation.setCycleCount(-1);
        animation.setAutoReverse(true);
        animation.play();*/
        Platform.runLater(() -> {
            //changeSpeakerImg("speaker_off.png");
            //changeMicImg("mic_off.png");
            robot_img.setImage(toImage("front_Duck.png"));
            //speaker_img.relocate(width/2-speaker_img.getImage().getWidth()*2, height-speaker_img.getImage().getHeight()-100);
            //mic_img.relocate(width/2+mic_img.getImage().getWidth(), height-mic_img.getImage().getHeight()-100);
            //pane.getChildren().addAll(speaker_img, mic_img);
        });
    }


    public Image toImage(String fileName) {
        return new Image(new File(imgPath + "/" + fileName).toURI().toString());
    }


}
