package com.cools.qr.converter;

import com.cools.qr.PhoneNumbers;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PhoneNumbersConverter implements CommandLine.ITypeConverter<PhoneNumbers> {

    private static final char DELIMITER = ';';

    @Override
    public PhoneNumbers convert(String phoneNumber) {
        if (StringUtils.isBlank(phoneNumber)) {
            throw new CommandLine.TypeConversionException("Missing or empty phone number");
        }

        String[] numbers = phoneNumber.split(String.valueOf(DELIMITER));

        if (ArrayUtils.isEmpty(numbers)) {
            throw new CommandLine.TypeConversionException(String.format("Invalid format for phone number string: " +
                                                                        "[%s], " +
                                                                        "must be " +
                                                                        "delimited with: [%s]", phoneNumber,
                                                                        DELIMITER));
        }

        return new PhoneNumbers(Arrays.stream(numbers).collect(Collectors.toSet()));
    }
}
