package in.coderarmy.hibernateDemo.model;

import jakarta.persistence.AttributeConverter;

public class BooleanToStringConverter
        implements AttributeConverter<Boolean, String> {

    @Override
    public String convertToDatabaseColumn(Boolean aBoolean) {

        if(aBoolean == null) return null;

        return aBoolean ? "Yes" : "No";
    }

    @Override
    public Boolean convertToEntityAttribute(String s) {

        if(s == null) return null;

        return "Yes".equals(s);
    }
}
