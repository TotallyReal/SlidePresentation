/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.markers;

//import animationEditor.animationData.AnimationData;
import javafx.animation.Animation;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import presentation.animation.AnimationData;

/**
 *
 * @author eofir
 */
public interface MarkerSetInterface {
    
    public void setMarkerName(String name);
    
    public String getMarkerName();
    
    public int getMarkerStart();
    
    public void setMarkerStart(int millis);
    
    public IntegerProperty markerStartProperty();
    
    public int getMarkerEnd();
    
    public void setMarkerEnd(int millis);
    
    public IntegerProperty markerEndtProperty();
    
    public boolean isCompressed();
    
    public void setCompressed(boolean compressed);
    
    public void copy(MarkerSetInterface set);
    
    public void setStatus(ReadOnlyObjectProperty<Animation.Status> status);
    
    public ReadOnlyObjectProperty<Animation.Status> statusProperty();

    public AnimationData getAnimationData();

    public void setAnimationData(AnimationData data);
    
}
