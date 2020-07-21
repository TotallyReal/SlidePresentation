package presentation.animation;

import java.util.Map;

/**
 *
 * @author eofir
 */
public interface AnimationHeader {
    
    /**
     * 
     * @return the name of the animation represented by this header. For example 
     * "Fade To"
     */
    public String animationName();
    
    /**
     * 
     * @return the type of this animation. For example, for "Fade To" animation
     * will return "Fade"
     */
    public String animationType();
    
    /**
     * 
     * @return the class of the object which this animation can animate. Returns 
     * null for parallel and sequential transition.
     */
    public Class objectClass();
    
    /**
     * Returns a map from the parameter names to their Classes. 
     * For example in the "Fade To" animation we can have a ("FadeTo",double.class)
     * entry.
     * @return 
     */
    public Map<String, Class> getParameterTypes();
    
    /**
     * Creates a new AnimationData for the animation represented by this header.
     * @return 
     */
    public AnimationData createData();
    
    
}
