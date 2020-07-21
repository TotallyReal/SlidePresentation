package presentation.animation;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author eofir
 */
public class SimpleObjectData implements ObjectData {

    String name = "empty", type = "general";
    Class cls = Object.class;

    public SimpleObjectData(String name, String type, Class cls) {
        if (name != null && type != null && cls != null) {
            this.name = name;
            this.type = type;
            this.cls = cls;
        }
    }


    @Override
    public Class getObjectClass() {
        return cls;
    }

    @Override
    public String getObjectName() {
        return name;
    }

    @Override
    public String getObjectType() {
        return type;
    }

    @Override
    public String toString(){
        return "("+getObjectClass().getSimpleName()+"):"+getObjectName()+" - "+getObjectType();
    }

    @Override
    public List<ObjectData> getSubObjects() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
