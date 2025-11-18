package com.planprostructure.planpro.components;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.postgresql.util.PGobject;
import java.sql.SQLException;
import java.util.List;

@Converter
public class JsonbConverter implements AttributeConverter<List<String>, PGobject> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public PGobject convertToDatabaseColumn(List<String> attribute) {
        if (attribute == null) return null;

        try {
            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");
            jsonObject.setValue(objectMapper.writeValueAsString(attribute));
            return jsonObject;
        } catch (JsonProcessingException | SQLException e) {
            throw new IllegalArgumentException("Error converting list to JSONB", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(PGobject dbData) {
        if (dbData == null) return null;

        try {
            return objectMapper.readValue(dbData.getValue(), new TypeReference<List<String>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Error converting JSONB to List<String>", e);
        }
    }
}
