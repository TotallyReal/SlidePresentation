package presentation.animation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eofir
 */
public class ObjectPath {
    
    private final AnimationNode root;
    private final List<ObjectData> path;
    private ObjectData current;

    /**
     * create a new ObjectPath rooted at the given argument
     * @param root 
     */
    public ObjectPath(AnimationNode root){
        this.root = root;
        path = new ArrayList(5);
        current = new SimpleObjectData("root", "general", root.getClass());
        path.add(current);
    }
    
    /**
     * Return the number of element in the path from the root to this object
     * 
     * @return 
     */
    public int pathLength(){
        return path.size();
    }
    
    /**
     * Return the ObjectData in the path at the given index
     * @param index
     * @return 
     */
    public ObjectData dataAt(int index){
        if (index<0 || index>=pathLength())
            return null;
        return path.get(index);
        
    }
    
    /**
     * duplicate this instance of ObjectPath
     * @return 
     */
    public ObjectPath duplicate() {
        ObjectPath dup = new ObjectPath(root);
        Iterator<ObjectData> it = path.iterator();
        it.next();
        while (it.hasNext()){
            ObjectData next = it.next();
            dup.chooseChild(next.getObjectName());
        }        
        return dup;
    }
    
    /**
     * 
     * @return an ObjectData pointed by this path
     */
    public ObjectData getCurrentObject(){
        return current;
    }
    
    /**
     * 
     * @return the class of the object pointed by this path
     */
    public Class getClassType(){
        return getCurrentObject().getObjectClass();
    }
    
    /**
     * go up one step in this path.
     * @return true if can go up, and false if it is the path has length 1.
     */
    public boolean goUp(){
        if (path.size()==1)
            return false;
        path.remove(path.size()-1);
        current = path.get(path.size()-2);
        return true;        
    }
    
    /**
     * choose the next element in the path to be the one with the specified name.
     * If the name exists, add it to the path and return true. Otherwise return false.
     * 
     * @param name the name of the next element in the path
     * @return true if the name is valid
     */
    public boolean chooseChild(String name){
        List<ObjectData> list = getObjectChildren();
        if (list==null || name == null)
            return false;
        Optional<ObjectData> findFirst = list.stream().filter(data->data.getObjectName().equals(name))
                .findFirst();
        if (findFirst.isPresent()){
            current = findFirst.get();
            path.add(current);
            return true;
        }
        return false;
    }
    
    public ObjectPath pathToChild(String name){
        ObjectPath dup = duplicate();
        boolean exists = dup.chooseChild(name);
        if (exists){
            return dup;
        } else {
            return null;
        }
    }
    
    static final List<ObjectData> EMPTY_LIST = new ArrayList<ObjectData>();
    
    /**
     * 
     * @return a list of the children of the object pointed by this path
     */
    public List<ObjectData> getObjectChildren(){
        Class cls = current.getObjectClass();
        if (AnimationNode.class.isAssignableFrom(cls)){
            try {
                Method method = cls.getMethod("getSubobjects");
                return  (List<ObjectData>) method.invoke(null);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException 
                    | IllegalArgumentException | InvocationTargetException ex) {
                Logger.getLogger(ObjectPath.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return Collections.unmodifiableList(EMPTY_LIST);
    }
    
    /**
     * Compute the object that this path points to.
     * This function should be called only in "runtime", when the actual object
     * is needed and not just a path to it.
     * @param root
     * @return 
     */
    public Object computeObject(AnimationNode root){
        Iterator<ObjectData> it = path.iterator();
        it.next();
        AnimationNode temp = root;
        while (it.hasNext()){
            ObjectData next = it.next();
            Class cls = next.getObjectClass();
            Object subObject = temp.getSubobject(next.getObjectName());
            if (subObject==null)
                return null;
            if (AnimationNode.class.isAssignableFrom(cls)){
                temp = (AnimationNode)subObject;
            } else {
                return subObject;
            }
        }
        return temp;
    }
    
    
    
    /**
     * If the parameter object is a ObjectPath, return the object that it is 
     * pointing to, and otherwise return the object itself.
     * 
     * @param object
     * @return 
     */
    public static Object getObject(AnimationNode root, Object object){
        if (object==null || root==null)
            return  null;
        if (object instanceof ObjectPath)
            return ((ObjectPath)object).computeObject(root);
        return object;
    }
    
    /**
     * Returns true if obj is an instance of cls, or if it is an object path 
     * pointing to such an object. Also it doesn't distinguish between primitives
     * and their object boxes.
     * If obj==null and cls is not null and not a primitive, then return true.
     * 
     * @param obj
     * @param cls
     * @return 
     */
    public static boolean objInstance(Object obj, Class cls){
        if (cls==null)
            return false;
        if (obj==null)
            return !cls.isPrimitive();
        Class objClass = obj.getClass();        
        if (obj instanceof ObjectPath){
            objClass = ((ObjectPath)obj).getCurrentObject().getObjectClass();
        }        
        return inherits(objClass, cls);
    }

    public static Object numberInstance(Object object, Class cls) {
        if (object==null || cls==null)
            return false;
        Class objCls = object.getClass();
        if (Number.class.isAssignableFrom(objCls)){
            Number number = (Number) object;
            cls = fromPrimitive(cls);
            if (Double.class.equals(cls)){
                return number.doubleValue();
            }
            if (Integer.class.equals(cls)){
                return number.intValue();
            }
            if (Byte.class.equals(cls)){
                return number.byteValue();
            }
            if (Float.class.equals(cls)){
                return number.floatValue();
            }
            if (Long.class.equals(cls)){
                return number.longValue();
            }
            if (Short.class.equals(cls)){
                return number.shortValue();
            }
        }
        return null;
    }
    
    /**
     * Returns true if supClass is a super class (or interface) of sub class.
     * This is basically Class.isAssignableFrom, but it will also return true
     * if one class is primitive and the other is its object box.
     * 
     * @param supClass
     * @param subClass
     * @return 
     */
    public static boolean inherits(Class subClass, Class supClass){
        if (supClass == null || subClass ==null)
            return false;
        supClass = fromPrimitive(supClass);
        subClass = fromPrimitive(subClass);
        return supClass.isAssignableFrom(subClass);
    }
    
    private static Class fromPrimitive(Class cls) {
        if (cls.equals(double.class)) {
            return Double.class;
        }
        if (cls.equals(int.class)) {
            return Integer.class;
        }
        if (cls.equals(boolean.class)) {
            return Boolean.class;
        }
        if (cls.equals(float.class)) {
            return Float.class;
        }
        if (cls.equals(byte.class)) {
            return Byte.class;
        }
        if (cls.equals(char.class)) {
            return Character.class;
        }
        if (cls.equals(long.class)) {
            return Long.class;
        }
        if (cls.equals(short.class)) {
            return Short.class;
        }
        return cls;
    }
    
    @Override
    public String toString(){
        return getCurrentObject().getObjectName();
    }

    
}
