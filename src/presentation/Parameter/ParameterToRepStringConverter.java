/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentation.Parameter;

/**
 *
 * @author eofir
 */
public interface ParameterToRepStringConverter <T> {
    
    public String convert(T obj);
    
}
