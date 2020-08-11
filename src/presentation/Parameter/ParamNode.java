package presentation.Parameter;

import java.util.HashMap;
import java.util.Map;
import javafx.beans.property.Property;
import javafx.scene.Node;
import presentation.Parameter.Parameter;
import presentation.Parameter.Parameterizable;

/**
 *
 * @author eofir
 */
public class ParamNode implements Parameterizable{

  protected Node node;
  protected Map<String, Property> map;
  protected String nodeName;
  
  public ParamNode(Node node, String name){
    this.node = node;   
    this.nodeName = name;
    map = new HashMap<>();
    if (node == null || name == null || name.equals(""))
      return; //some error!
  }
  
  public void addLocationParameters(){
    addParameter("layoutX", node.layoutXProperty());
    addParameter("layoutY", node.layoutYProperty());
    addParameter("translateX", node.translateXProperty());
    addParameter("translateY", node.translateYProperty());    
  }
  
  public void addParameter(String paramName, Property prop){
    map.put(nodeName+"."+paramName, prop);
  }
  
  @Override
  public void saveTo(Parameter param) {
    if (node==null)
      return;
    for (Map.Entry<String, Property> entry : map.entrySet()){
      param.setParameterByPath(entry.getKey(), entry.getValue().getValue());
    }
    /*param.setParameterByPath("layoutX", node.getLayoutX());
    param.setParameterByPath("layoutY", node.getLayoutY());
    param.setParameterByPath("translateX", node.getTranslateX());
    param.setParameterByPath("translateY", node.getTranslateY());*/
  }
  
  public void createDefaults(Parameter param) {
    if (node == null)
      return;
    for (Map.Entry<String, Property> entry : map.entrySet()){
      Parameter p = param.search(entry.getKey(), true);
      if (p.isEmpty()){
        Object obj = entry.getValue().getValue();
        p.setValue(obj);
        p.setType(Parameter.objectType(obj));
      }
    }
  }

  @Override
  public void loadFrom(Parameter param) {
    for (Map.Entry<String, Property> entry : map.entrySet()){
      entry.getValue().setValue(param.C(entry.getKey()));
    }
    /*node.setLayoutX((double)param.C("layoutX"));
    node.setLayoutX((double)param.C("layoutY"));
    node.setLayoutX((double)param.C("translateX"));
    node.setLayoutX((double)param.C("translateY"));*/
  }
  
}