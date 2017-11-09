/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package webshop.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 *
 * @author hom
 */
@FacesConverter( "localdate" )
public class DateConverter implements Converter {

    @Override
    public Object getAsObject( FacesContext context, UIComponent component,
            String value ) {
        LocalDate result =null;
        if ( null != value && !value.isEmpty()) {
            result= LocalDate.parse(value);
        }
        return result;
    }

    @Override
    public String getAsString( FacesContext context, UIComponent component,
            Object value ) {
        if ( null != value ) {
            return ( (LocalDate) value ).format( DateTimeFormatter.ISO_DATE );
        }
        return "";
    }

}
