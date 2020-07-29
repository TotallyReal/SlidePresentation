package presentation.Parameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;


public class Parameter implements java.io.Serializable {

  public static Parameter createRootParameter() {
    return new Parameter("ROOT", "Category", new ArrayList<Parameter>());
  }

  public Parameter() {
    this.name = new SimpleStringProperty("no name");
    this.value = new SimpleObjectProperty<>(null);
    this.type = new SimpleStringProperty("Category");

  }

  public Parameter(String name, String type, Object value) {
    this.name = new SimpleStringProperty(name);
    this.value = new SimpleObjectProperty<>(value);
    this.type = new SimpleStringProperty(type);
  }

  /**
   * Create a parameter from a representing string
   *
   * @param repString
   */
  public Parameter(String repString) {
    this();
    copyParameter(Parameter.loadFromRepString(repString));
  }

  /**
   * Copies the given parameter if not null
   *
   * @param param
   */
  public void copyParameter(Parameter param) {
    if (param == null) {
      return;
    }
    setValue(param.getValue());
    setType(param.getType());
    setName(param.getName());
  }

  // <editor-fold defaultstate="collapsed" desc="Parameter Data">
  private final StringProperty name;
  private final StringProperty type;
  private final ObjectProperty<Object> value;

  public String getName() {
    return name.get();
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getType() {
    return type.get();
  }

  public void setType(String type) {
    this.type.set(type);
  }

  public Object getValue() {
    return value.get();
  }

  public void setValue(Object value) {
    this.value.set(value);
  }

  public StringProperty getNameProperty() {
    return name;
  }

  public StringProperty getTypeProperty() {
    return type;
  }

  public ObjectProperty<Object> getValueProperty() {
    return value;
  }

  // </editor-fold>
  //
  // <editor-fold defaultstate="collapsed" desc="parameter user interface">
  /**
   * Returns the parameter for the given parameter name.
   *
   * The name should include the path to the parameter separated by dots. For
   * example, if there is a radius parameter inside the category param1, then
   * the full parameter name should be param1.radius .
   *
   * @param paramName
   * @return
   */
  public Object C(String paramName) {
    return C(paramName, null);
  }

  /**
   * Returns the parameter for the given parameter name.The name should include the path to the parameter separated by dots.
   *
   * For
 example, if there is a radius parameter inside the category param1, then
 the full parameter name should be param1.radius. In case this parameter
 doesn't exist, create it and set its value to be defObj.
   *
   * @param paramName
   * @param defObj
   * @return
   */
  public Object C(String paramName, Object defObj) {
    boolean create = (defObj != null);
    String typeS = objectType(defObj);
    if (paramName==null || (create && typeS==null)){
      return null;
    }
    Parameter param = search(paramName, create);
    //the parameter should never be empty, unless it was just created!
    if (param.isEmpty() && create){ 
      param.setValue(defObj);
      param.setType(typeS);
    }
    return param.getValue();
  }

  private static Parameter findChild(List<Parameter> list, String name) {
    Optional<Parameter> first
            = list.stream()
                    .filter(p -> name.equals(p.getName()))
                    .findFirst();
    if (first.isPresent()) {
      return first.get();
    } else {
      return null;
    }
  }

  /**
   * Search for a parameter with the given path.
   *
   * If the current parameter is ROOT.aa, and the path is bb.cc, then it
   * searches for ROOT.aa.bb.cc. If ROOT.aa is not a category parameter, will
   * always fail. Set create to true to create this path if it doesn't exist.
   * Note that in this case the returned parameter will be empty
   * (isEmpty()==true).
   *
   * @param paramName
   * @param create
   * @return
   */
  public Parameter search(String paramName, boolean create) {
    if (paramName == null || !this.isCategory()) {
      return null;
    }

    String[] split = paramName.split("\\.");
    for (String s : split) {
      if (s.equals("")) { //should never be empty
        return null;
      }
    }

    List<Parameter> list = (List<Parameter>) getValue();
    int index = 0;

    Parameter child = findChild(list, split[index]);

    while (child != null) {
      if (index == split.length - 1) {
        return child;
      }
      if (!child.isCategory()) //need to continue, but this is not a category!
      {
        return null;
      }
      list = (List<Parameter>) child.getValue();
      index++;
      child = findChild(list, split[index]);
    }

    if (create) {
      return createObject(list, split, index);
    } else {
      return null;
    }
  }

  /**
   * Finds the parameter given by the path (and creates it if necessary), and 
   * then set it to the given obj.
   * 
   * @param paramName
   * @param obj
   * @return 
   */
  public boolean setParameterByPath(String paramName, Object obj) {
    String typeS = objectType(obj);
    if (obj == null || typeS == null) {
      return false;
    }
    Parameter param = search(paramName, true);
    param.setType(typeS);
    param.setValue(obj);
    return true;
  }

  public static String objectType(Object obj) {
    if (obj == null) {
      return null;
    }
    if (obj instanceof String) {
      return "String";
    }
    if (obj instanceof Double) {
      return "Double";
    }
    if (obj instanceof Integer) {
      return "Integer";
    }
    if (obj instanceof Boolean) {
      return "Boolean";
    }
    return null;
  }

  private static Object stringToValue(String value, String type) {
    switch (type) {
      case "Boolean":
        return value.equals("true");
      case "Integer":
        return Integer.parseInt(value);
      case "Double":
        return Double.parseDouble(value);
      default:
        return value;
    }
  }

  /**
   * Creates a new parameter described by the path, starting at index, and put
   * it inside the list
   *
   * @param list
   * @param path
   * @param index
   * @return
   */
  private static Parameter createObject(List<Parameter> list, String[] path, int index) {
    for (; index < path.length - 1; index++) {
      List<Parameter> childList = new ArrayList<Parameter>();
      Parameter param = new Parameter(path[index], "Category", childList);
      list.add(param);
      list = childList;
    }

    Parameter param = new Parameter(path[index], null, null);
    list.add(param);
    return param;
  }

  public boolean isEmpty() {
    return getType() == null;
  }

  // </editor-fold>
  //
  // <editor-fold defaultstate="collapsed" desc="Representing Strings">
  public static Parameter loadFromRepString(String s) {
    s = s.replace("\t", "");
    SubString str = new SubString();
    str.str = s;
    str.begin = 0;
    return Parameter.loadFromRepSubString(str);
  }

  private static Parameter loadFromRepSubString(SubString s) {
    if (s.str.charAt(s.begin) == '}') {
      s.begin += 2; //skipi the '}\n'
      return null;
    }
    Parameter param = new Parameter();
    param.setType(s.getWord(','));
    param.setName(s.getWord('='));
    if (param.isCategory()) {
      s.begin += 2; //skip the '{\n' characters
      List<Parameter> list = new ArrayList<Parameter>();
      param.setValue(list);
      Parameter child = Parameter.loadFromRepSubString(s);
      while (child != null) {
        list.add(child);
        child = Parameter.loadFromRepSubString(s);
      }
    } else {
      param.setValue(stringToValue(s.getWord('\n'), param.getType()));
    }
    return param;
  }

  public String getRepString() {
    return getRepString("");
  }

  private String getRepString(String initial) {
    StringBuilder buffer = new StringBuilder();
    if (isCategory()) {
      buffer.append(initial).append("Category,").append(getName()).append("={\n");
      List<Parameter> list = (List<Parameter>) getValue();
      String inner = "\t" + initial;
      for (Parameter param : list) {
        buffer.append(param.getRepString(inner));
      }
      buffer.append(initial).append("}\n");
      return buffer.toString();
    }
    return initial + getType() + "," + getName() + "=" + getValue() + "\n";

  }

  private static class SubString {

    String str;
    int begin;

    String getWord(char ch) {
      int index = str.indexOf(ch, begin);
      String word = str.substring(begin, index).trim();
      begin = index + 1;
      return word;
    }

    public String toString() {
      return str.substring(begin);
    }
  }

  // </editor-fold>
  //
  // <editor-fold defaultstate="collapsed" desc="MISC">
  public boolean isCategory() {
    return getType().equals("Category");
  }

  @Override
  public String toString() {
    if (isCategory()) {
      return "Category, " + getName() + ": size = " + ((List) getValue()).size();
    } else {
      return getType() + ", " + getName() + "=" + getValue();
    }
  }

  // </editor-fold>
}
