// package com.planprostructure.planpro.components;

// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.planprostructure.planpro.domain.proTalk.MessageMetadata;
// import jakarta.persistence.AttributeConverter;
// import jakarta.persistence.Converter;

// @Converter
// public class MessageMetadataConverter implements AttributeConverter<MessageMetadata, String> {
//     private static final ObjectMapper objectMapper = new ObjectMapper();

//     @Override
//     public String convertToDatabaseColumn(MessageMetadata attribute) {
//         try {
//             return objectMapper.writeValueAsString(attribute);
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException("Could not convert metadata to JSON", e);
//         }
//     }

//     @Override
//     public MessageMetadata convertToEntityAttribute(String dbData) {
//         try {
//             return objectMapper.readValue(dbData, MessageMetadata.class);
//         } catch (JsonProcessingException e) {
//             throw new RuntimeException("Could not convert JSON to metadata", e);
//         }
//     }
// }
