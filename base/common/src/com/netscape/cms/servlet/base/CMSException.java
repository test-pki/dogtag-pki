package com.netscape.cms.servlet.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class CMSException extends RuntimeException {

    private static final long serialVersionUID = 6000910362260369923L;

    public int code;

    public CMSException(String message) {
        super(message);
        code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    public CMSException(int code, String message) {
        super(message);
        this.code = code;
    }

    public CMSException(Response.Status status, String message) {
        super(message);
        code = status.getStatusCode();
    }

    public CMSException(String message, Throwable cause) {
        super(message, cause);
        code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
    }

    public CMSException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public CMSException(Response.Status status, String message, Throwable cause) {
        super(message, cause);
        code = status.getStatusCode();
    }

    public CMSException(Data data) {
        super(data.message);
        code = data.code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Data getData() {
        Data data = new Data();
        data.className = getClass().getName();
        data.code = code;
        data.message = getMessage();
        return data;
    }

    @XmlRootElement(name="CMSException")
    public static class Data {

        @XmlElement(name="ClassName")
        public String className;

        @XmlElement(name="Code")
        public int code;

        @XmlElement(name="Message")
        public String message;

        @XmlElement(name="Attributes")
        @XmlJavaTypeAdapter(MapAdapter.class)
        public Map<String, String> attributes = new LinkedHashMap<String, String>();

        public String getAttribute(String name) {
            return attributes.get(name);
        }

        public void setAttribute(String name, String value) {
            attributes.put(name, value);
        }
    }

    public static class MapAdapter extends XmlAdapter<AttributeList, Map<String, String>> {

        public AttributeList marshal(Map<String, String> map) {
            AttributeList list = new AttributeList();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                Attribute attribute = new Attribute();
                attribute.name = entry.getKey();
                attribute.value = entry.getValue();
                list.attributes.add(attribute);
            }
            return list;
        }

        public Map<String, String> unmarshal(AttributeList list) {
            Map<String, String> map = new LinkedHashMap<String, String>();
            for (Attribute attribute : list.attributes) {
                map.put(attribute.name, attribute.value);
            }
            return map;
        }
    }

    public static class AttributeList {
        @XmlElement(name="Attribute")
        public List<Attribute> attributes = new ArrayList<Attribute>();
    }

    public static class Attribute {

        @XmlAttribute
        public String name;

        @XmlValue
        public String value;
    }

    @Provider
    public static class Mapper implements ExceptionMapper<CMSException> {

        public Response toResponse(CMSException exception) {
            // convert CMSException into HTTP response with XML content
            return Response
                    .status(exception.getCode())
                    .entity(exception.getData())
                    .type(MediaType.TEXT_XML)
                    .build();
        }
    }

    public static void main(String args[]) throws Exception {
        Data data = new Data();
        data.className = CMSException.class.getName();
        data.code = Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
        data.message = "An error has occured";
        data.setAttribute("attr1", "value1");
        data.setAttribute("attr2", "value2");

        JAXBContext context = JAXBContext.newInstance(Data.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(data, System.out);
    }
}