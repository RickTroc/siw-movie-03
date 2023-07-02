package it.uniroma3.siw.controller.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageValidator implements Validator {
    public static final long MAX_SIZE = 500 * 1024;

    @Override
    public boolean supports(Class<?> aClass) {
        return MultipartFile.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        MultipartFile image = (MultipartFile) o;
        if (!isImage(image) || image.getSize() > MAX_SIZE) {
            errors.reject("image.format");
        }
    }

    public boolean isImage(MultipartFile image) {
        return image.getContentType().startsWith("image/");
    }

 
}
