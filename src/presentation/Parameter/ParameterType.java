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
public class ParameterType<T> {
    
    ParameterToValueConverter<T> pvConv;
    ParameterToRepStringConverter<T> prConv;    
    String name;
    Class cls;
    
    public ParameterType(String typeName, Class typeClass, 
            ParameterToValueConverter<T> pvConv, ParameterToRepStringConverter<T> prConv){
        name = typeName;
        cls = typeClass;
        this.pvConv = pvConv;
        this.prConv = prConv;
    }
    
    public T convertToValue(String objStr){
        return pvConv.convert(objStr);
    }
    
    public String convertToString(T obj){
        return prConv.convert(obj);
    }
    
    @Override
    public String toString(){
        return "Parameter("+name+")";
    }
    
}

