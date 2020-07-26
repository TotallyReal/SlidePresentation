/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation;

import javafx.collections.ObservableList;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author eofir
 */
public class DragPane extends Group {

    public DragPane() {
        super();
        //this.getChildren().add(here);///???? initialized?
    }

    public boolean addDragAll(Node... nodes) {
        //getChildren().remove(here);
        boolean b = this.getChildren().addAll(nodes);
        for (Node node : nodes){
            enableDrag(node);
        }        
        //getChildren().add(here);
        return b;
    }

    public void addDrag(Node node) {
        //getChildren().remove(here);
        this.getChildren().add(node);
        enableDrag(node);
        //getChildren().add(here);
    }

    public void rotateNodes() {
        ObservableList<Node> children = getChildren();
        Node node = children.remove(0);
        children.add(children.size() - 1, node);
       // firstNode = node;
    }

    static class Delta {

        double x, y;
    }
/*
    private static class DragContext {

        double mouseAnchorX, mouseAnchorY, initialTranslateX, initialTranslateY;
    }*/

    private void enableDrag(final Node node) {
        final Delta dragDelta = new Delta();
        final Delta original = new Delta();
        node.setOnMousePressed((MouseEvent mouseEvent) -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = mouseEvent.getSceneX();
            dragDelta.y = mouseEvent.getSceneY();
            original.x = node.getLayoutX();
            original.y = node.getLayoutY();
            node.getScene().setCursor(Cursor.MOVE);
            mouseEvent.consume();
        });
        node.setOnMouseReleased((MouseEvent mouseEvent) -> {
            node.getScene().setCursor(Cursor.HAND);
            System.out.println(node.toString()+": new coordinates (layout) = ("+node.getLayoutX()+","+
                    node.getLayoutY()+")");
            System.out.println(node.toString()+": new coordinates (translate) = ("+node.getTranslateX()+","+
                    node.getTranslateY()+")");
            mouseEvent.consume();
        });
        node.setOnMouseDragged((MouseEvent mouseEvent) -> {
            node.setLayoutX(original.x+mouseEvent.getSceneX()- dragDelta.x);
            node.setLayoutY(original.y+mouseEvent.getSceneY()- dragDelta.y);
//            node.setTranslateX(mouseEvent.getSceneX() - dragDelta.x);            
//            node.setTranslateY(mouseEvent.getSceneY() - dragDelta.y);
            mouseEvent.consume();
        });

    }
    
    public void addZoomable(Node node){
        
        node.setOnScroll(evt->{
            double deltaY = Math.pow(2, evt.getDeltaY()/100);
            node.setScaleX(node.getScaleX()*deltaY);
            node.setScaleY(node.getScaleY()*deltaY);
        });
        
    }
}
