package com.cools.qr.converter;

import ezvcard.parameter.ImageType;
import ezvcard.property.Photo;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class PhotoConverter implements CommandLine.ITypeConverter<Photo> {

    @Override
    public Photo convert(String filePath) throws Exception {
        if (StringUtils.isBlank(filePath)) {
            throw new CommandLine.TypeConversionException("Missing filepath");
        }

        File logoFile = new File(getClass().getClassLoader().getResource(filePath).toURI());

        BufferedImage bufferedImage = Thumbnails.of(logoFile).scale(1.0d).outputQuality(1.0d).asBufferedImage();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        File imgFile = new File("resized_image.png");   // File path for output
        ImageIO.write(bufferedImage, "png", imgFile);              // Write image to file
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);

        return new Photo(byteArrayOutputStream.toByteArray(), ImageType.PNG);
    }
}
