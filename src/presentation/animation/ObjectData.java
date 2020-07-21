package presentation.animation;

import java.util.List;

/**
 *
 * @author eofir
 */
public interface ObjectData {

    /**
     * The class of the object described by this ObjectData.
     * 
     * @return the class of the described object.
     */
    public Class getObjectClass();

    /**
     * 
     * @return the name of the described object
     */
    public String getObjectName();

    /**
     * 
     * @return the type of the described object
     */
    public String getObjectType();

    /**
     * 
     * @return A list of the subobjects
     */
    public List<ObjectData> getSubObjects();
    
    /**
     * returns true if this object data has the same class as the object data
     * in the parameter.
     * 
     * @param data
     * @return 
     */
    default public  boolean sameAs(ObjectData data){
        return (data!=null && getObjectClass().equals(data.getObjectClass()) 
                && getObjectName().equals(data.getObjectName()));
    }
    
    /*default public List<ObjectData> getSubObjects() {
        Class cls = getObjectClass();
        if (AnimationNode.class.isAssignableFrom(cls)) {
            try {
                Method method = cls.getMethod("getSubobjects");
                Object returnValue = method.invoke(null);
                return (List<ObjectData>) returnValue;
            } catch (NoSuchMethodException | SecurityException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                Logger.getLogger(SimpleObjectData.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }*/
    

}
