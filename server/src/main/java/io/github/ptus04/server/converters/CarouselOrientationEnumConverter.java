package io.github.ptus04.server.converters;

import io.github.ptus04.server.enums.CarouselOrientationEnum;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
class CarouselOrientationEnumConverter implements Converter<String, CarouselOrientationEnum> {

    @Override
    public CarouselOrientationEnum convert(String source) {
        return CarouselOrientationEnum.valueOf(source.toUpperCase());
    }
}
