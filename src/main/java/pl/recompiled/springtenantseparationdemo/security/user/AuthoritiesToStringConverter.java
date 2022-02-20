package pl.recompiled.springtenantseparationdemo.security.user;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Converter
class AuthoritiesToStringConverter implements AttributeConverter<Set<Authority>, String> {

    @Override
    public String convertToDatabaseColumn(Set<Authority> objects) {
        return objects.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }

    @Override
    public Set<Authority> convertToEntityAttribute(String s) {
        return Arrays.stream(s.split(","))
                .map(Authority::valueOf)
                .collect(Collectors.toSet());
    }

}
