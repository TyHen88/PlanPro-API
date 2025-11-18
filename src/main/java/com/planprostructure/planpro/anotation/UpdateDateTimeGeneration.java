//package com.planprostructure.planpro.anotation;
//
//import com.planprostructure.planpro.utils.DateTimeUtils;
//import org.hibernate.tuple.AnnotationValueGeneration;
//import org.hibernate.tuple.GenerationTiming;
//import org.hibernate.tuple.ValueGenerator;
//
//public class UpdateDateTimeGeneration implements AnnotationValueGeneration<UpdateDateTime> {
//    private ValueGenerator<?> generator;
//
//    @Override
//    public void initialize(UpdateDateTime annotation, Class<?> propertyType) {
//        generator = ((session, owner) -> DateTimeUtils.ictNow().format(DateTimeUtils.VA_FORMATTER_DTM14));
//    }
//
//    @Override
//    public GenerationTiming getGenerationTiming() {
//        return GenerationTiming.ALWAYS;
//    }
//
//    @Override
//    public ValueGenerator<?> getValueGenerator() {
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
