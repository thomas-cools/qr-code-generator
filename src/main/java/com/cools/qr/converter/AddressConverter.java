package com.cools.qr.converter;

import ezvcard.property.Address;
import org.apache.commons.lang3.ArrayUtils;
import picocli.CommandLine;

public class AddressConverter implements CommandLine.ITypeConverter<Address> {

    @Override
    public Address convert(String s) {
        if (s.isBlank()) {
            throw new CommandLine.TypeConversionException("Address string is empty");
        }
        String[] addressValues = s.split(":");
        if (ArrayUtils.isEmpty(addressValues) || addressValues.length < 5) {
            throw new CommandLine.TypeConversionException(String.format("Address string is invalid: [%s]. Usage is " +
                                                                        "streetAddress:city:province:country:postcode",
                                                                        s));
        }

        Address address = new Address();
        address.setStreetAddress(addressValues[0]);
        address.setLocality(addressValues[1]);
        address.setRegion(addressValues[2]);
        address.setCountry(addressValues[3]);
        address.setPostalCode(addressValues[4]);

        return address;
    }
}
