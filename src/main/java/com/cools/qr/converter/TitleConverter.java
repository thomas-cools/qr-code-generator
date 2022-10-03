package com.cools.qr.converter;

import ezvcard.property.Title;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

public class TitleConverter implements CommandLine.ITypeConverter<Title> {

    @Override
    public Title convert(String title) {
        if (StringUtils.isEmpty(title)) {
            throw new CommandLine.TypeConversionException("Role is empty or blank");
        }

        return new Title(title);
    }
}
