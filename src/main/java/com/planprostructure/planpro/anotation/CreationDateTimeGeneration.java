//package com.planprostructure.planpro.anotation;
//
//import com.planprostructure.planpro.utils.DateTimeUtils;
//import org.hibernate.tuple.AnnotationValueGeneration;
//import org.hibernate.tuple.GenerationTiming;
//import org.hibernate.tuple.ValueGenerator;
//
//public class CreationDateTimeGeneration implements AnnotationValueGeneration<CreationDateTime> {
//    private ValueGenerator<String> generator;
//
//    @Override
//    public void initialize(CreationDateTime annotation, Class<?> propertyType) {
//        generator = (session, owner) -> DateTimeUtils.ictNow().format(DateTimeUtils.VA_FORMATTER_DTM14);
//    }
//
//    @Override
//    public GenerationTiming getGenerationTiming() {
//        return GenerationTiming.INSERT;
//    }
//
//    @Override
//    public ValueGenerator<String> getValueGenerator() {
//        return generator;
//    }
//
//    @Override
//    public boolean referenceColumnInSql() {
//        return false;
//    }
//
//    @Override
//    public String getDatabaseGeneratedReferencedColumnValue() {
//        return null;
//    }
//}
