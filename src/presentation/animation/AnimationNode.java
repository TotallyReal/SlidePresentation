package presentation.animation;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javafx.animation.Animation;

/**
 *
 * @author eofir
 */
public interface AnimationNode {

    /**
     * Returns the subobject with the specified name. 
     *
     * @param name
     * @return the given subobject with the specified name
     */
    public Object getSubobject(String name);

    /**
     * returns a list of the attainable subobjects of this object.
     * As it is right now, returns an empty list. Override (hide) this method
     * if there are subobjects
     * @return a list of subobjects of this class.
     */
    public static List<ObjectData> getSubobjects(){
        return new ArrayList<>();
    } 
    
    public static List<Method> getSpecificAnimations(Class cls){
        Method[] methods = cls.getDeclaredMethods();
        Stream<Method> stream = Arrays.stream(methods);
        List<Method> animMethods = stream.filter(obj -> {
            Method method = (Method) obj;
            if (!Modifier.isPublic(method.getModifiers())) {
                return false;
            }
            Class<?> returnType = method.getReturnType();

            return (Animation.class.isAssignableFrom(returnType));
                    //|| VideoAnimation.class.isAssignableFrom(returnType));
        }).collect(Collectors.toList());
        return animMethods;
    }
    
}
