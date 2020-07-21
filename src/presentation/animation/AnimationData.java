package presentation.animation;

//import animationEditor.objectData.AnimationNode;
//import animationEditor.objectData.ObjectPath;
import java.util.List;
import javafx.animation.Animation;

/**
 *
 * @author eofir
 */
public interface AnimationData {       
    
    /**
     * Creates an animation from the data in this AnimationData object
     * 
     * @return the animation created, or null if there was a problem
     */
    public Animation createAnimation(AnimationNode root);
    
    /**
     * 
     * @return the header for this animation data
     */
    public AnimationHeader getHeader();
    
    /**
     * Get the parameter assigned to the given paramName.
     * The parameter can be either an ObjectPath points to the real parameter
     * or an object for one of the primitives or String.
     * 
     * @param paramName The name of the parameters
     * @return an ObjectPath, object of primitive or String
     */
    public Object getParameter(String paramName);
    
    /**
     * Set the parameter for the given paramName.
     * The object needs to fit the parameter type - it needs to be from a subclass
     * of the class of the parameters, or an ObjectPath pointing to such a class.
     * If this is not the case returns false.
     * Also, for primitive\String given without a path, this method can also check
     * if the parameter satisfies some criterion (e.g. the fade value is between
     * 0 and 1) and return true only if it does.
     * 
     * @param paramName Name of the parameter
     * @param object The parameter itself (for primitive and String), or path to it.
     * @return true if the parameter is set successfully.
     */
    public boolean setParameter(String paramName, Object object);
    
    /**
     * Set the nodes for this AnimationData
     * 
     * 
     * @param nodes
     * @return true if nodes were set correctly
     */
    public boolean setNodes(List<ObjectPath> nodes);
    
    /**
     * 
     * @return the list of object path leading to the animated nodes.
     */
    public List<ObjectPath> getNodes();
    
    /**
     * Add a node for this animation to animate.
     * Will return false if the node doesn't fit to this animation.
     * 
     * @param path The path pointing to the node
     * @return true if the node was set successfully. 
     */
    public boolean addNode(ObjectPath path);
    
    /**
     * Check if the animation animates the node in the given path, and if
     * so removes it.
     * 
     * @param path
     * @return true if removed successfully.
     */
    public boolean removeNode(ObjectPath path);
    
    
    
}
