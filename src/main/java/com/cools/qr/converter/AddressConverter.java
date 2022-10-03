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
        if (ArrayUtils.isEmpty(addressValues) || addressValues.length < 6) {
            throw new CommandLine.TypeConversionException(String.format("""
                                                                        Address string is invalid: [%s]. Usage is suite|houseNumber:streetAddress:city:province:country:postcode
                                                                        """, s));
        }

        Address address = new Address();
        address.setExtendedAddress(addressValues[0]);
        address.setStreetAddress(addressValues[1]);
        address.setLocality(addressValues[2]);
        address.setRegion(addressValues[3]);
        address.setCountry(addressValues[4]);
        address.setPostalCode(addressValues[5]);

        return address;
    }
}
