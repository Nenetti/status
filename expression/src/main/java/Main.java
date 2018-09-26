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

import org.apache.commons.logging.Log;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

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
import std_msgs.Int32;

/**
 * A simple {@link Subscriber} {@link NodeMain}.
 */
public class Main extends AbstractNodeMain {

	private String imgPath=System.getProperty("user.home")+"/ros/image/";
	private AnchorPane pane;
	private ImageView speaker_img;
	private ImageView mic_img;


	@Override
	public GraphName getDefaultNodeName() {
		return GraphName.of("status");
	}

	@Override
	public void onStart(ConnectedNode connectedNode) {
		setting();
		Subscriber<std_msgs.String> expression=connectedNode.newSubscriber("status/expression", std_msgs.String._TYPE);
		expression.addMessageListener(new MessageListener<std_msgs.String>() {
			@Override
			public void onNewMessage(std_msgs.String message) {

			}
		});
		Subscriber<std_msgs.String> mic=connectedNode.newSubscriber("status/mic", std_msgs.String._TYPE);
		mic.addMessageListener(new MessageListener<std_msgs.String>() {
			@Override
			public void onNewMessage(std_msgs.String message) {
				switch (message.getData()) {
				case "on":
					changeMicImg("mic_on.png");
					break;
				case "off":
					changeMicImg("mic_off.png");
					break;
				}
			}
		});
		Subscriber<std_msgs.String> speaker=connectedNode.newSubscriber("status/speaker", std_msgs.String._TYPE);
		speaker.addMessageListener(new MessageListener<std_msgs.String>() {
			@Override
			public void onNewMessage(std_msgs.String message) {
				switch (message.getData()) {
				case "on":
					changeSpeakerImg("speaker_on.png");
					break;
				case "off":
					changeSpeakerImg("speaker_off.png");
					break;
				}
			}
		});
	}

	public void setting() {
		final JFrame frame=new JFrame();
		Rectangle rectangle=GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		final double width=rectangle.getWidth();
		final double height=rectangle.getHeight();
		JFXPanel panel=new JFXPanel();
		frame.add(panel);
		pane=new AnchorPane();
		Scene scene=new Scene(pane, width, height);
		panel.setScene(scene);
		frame.setSize((int)width,(int)height);
		frame.setVisible(true);
		panel.setSize((int)width, (int)height);
		speaker_img=new ImageView();
		mic_img=new ImageView();
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				changeSpeakerImg("speaker_off.png");
				changeMicImg("mic_off.png");
				speaker_img.relocate(width/2-speaker_img.getImage().getWidth()*2, height-speaker_img.getImage().getHeight()-100);
				mic_img.relocate(width/2+mic_img.getImage().getWidth(), height-mic_img.getImage().getHeight()-100);
				pane.getChildren().addAll(speaker_img, mic_img);
			}
		});
	}

	public void changeSpeakerImg(String fileName) {
		speaker_img.setImage(new Image(new File(imgPath+fileName).toURI().toString()));
	}
	public void changeMicImg(String fileName) {
		mic_img.setImage(new Image(new File(imgPath+fileName).toURI().toString()));
	}



}
