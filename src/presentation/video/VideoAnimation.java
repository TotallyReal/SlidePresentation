/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.video;

import javafx.event.EventHandler;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import presentation.slides.SlidesPane;

/**
 *
 * @author eofir
 */
public class VideoAnimation{

    //be careful with multiple clips from the same video file. Can mix up the rates.
    
    public MediaPlayer player;
    int start, end;
    double rate;
    VideoNode node;
    
    public VideoAnimation(VideoNode node, MediaPlayer player, int start, int end){
        this.node = node;
        this.player = player;
        this.start = start;
        this.end = end;
        rate = 1;
    }
    
    public void setOnFinished(EventHandler handler){
        player.setOnStopped(new Runnable(){
            @Override
            public void run() {
                handler.handle(null);
            }
        });
    }
    
    public void setOnFinished(Runnable runner){
        player.setOnStopped(runner);
    }

    //the rate relative to the VideoNode rate
    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
        player.setRate(getRate()*node.getRate()); 
    }
    
    public void jumpTo(double millis){
        
    }

    public void compressTo(double millis, boolean always){
        double currentTime = (end-start)/(getRate()*node.getRate());
        if (millis<currentTime || always){
            rate*=(currentTime/millis);
        } 
    }
    
    public void play() {
        //GOD DAMMIT - always change the stop time before calling seek!!!!!
        player.setStopTime(Duration.millis(end));
        player.seek(Duration.millis(start));
        player.play();
        SlidesPane.out.println("=== seeking time " + start +" ending time "+end);
        SlidesPane.out.println("=== rate is "+player.getRate());
    }
    
    public void unpause(){
        player.play();
    }

    public void pause() {
        player.pause();
    }
    
    public double getTime(){        
        return player.getCurrentTime().toMillis();
    }
    
    

    
}
