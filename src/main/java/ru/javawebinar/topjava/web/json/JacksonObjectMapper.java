package ru.javawebinar.topjava.web.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;

import java.time.LocalDateTime;

/**
 * <p>
 * Handling Hibernate lazy-loading
 *
 * @link https://github.com/FasterXML/jackson
 * @link https://github.com/FasterXML/jackson-datatype-hibernate
 * @link http://wiki.fasterxml.com/JacksonHowToCustomSerializers
 */
public class JacksonObjectMapper extends ObjectMapper {

    private static final ObjectMapper MAPPER = new JacksonObjectMapper();

    private JacksonObjectMapper() {
        registerModule(new Hibernate5Module());

        SimpleModule customModule = new SimpleModule("customModule");
        customModule.addSerializer(new JsonLocalDateTimeConverter.UserSettingSerializer());
        customModule.addDeserializer(LocalDateTime.class, new JsonLocalDateTimeConverter.UserSettingDeserializer());
        registerModule(customModule);

        setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public static ObjectMapper getMapper() {
        return MAPPER;
    }
}