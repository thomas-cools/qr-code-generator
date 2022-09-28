package com.cools.qr.converter;

import ezvcard.parameter.TelephoneType;
import picocli.CommandLine;

public class PhoneTypeConverter implements CommandLine.ITypeConverter<TelephoneType> {

    @Override
    public TelephoneType convert(String value) {
        switch (value) {
            case null -> throw new CommandLine.TypeConversionException("Missing phone type");
            case String s -> {
                TelephoneType telephoneType = TelephoneType.find(s);
                if (telephoneType == null) {
                    throw new CommandLine.TypeConversionException(String.format("Invalid phone type: [%s]", s));
                }
                return telephoneType;
            }
        }
    }
}
