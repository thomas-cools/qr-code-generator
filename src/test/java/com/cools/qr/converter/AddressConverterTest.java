package com.cools.qr.converter;

import ezvcard.property.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AddressConverterTest {

    private final AddressConverter addressConverter = new AddressConverter();

    @Test
    void testValidAddressParse() {
        String addressString = "Av Paseo de los Leones 101-Local 11 Cumbres Elite 5to. Sector:Monterrey:Nuevo " +
                               "Leon:Mexico:64349";
        Address address = assertDoesNotThrow(() -> addressConverter.convert(addressString));
        Assertions.assertNotNull(address);
        Assertions.assertEquals("Av Paseo de los Leones 101-Local 11 Cumbres Elite 5to. Sector",
                                address.getStreetAddress());
        Assertions.assertEquals("Monterrey", address.getLocality());
        Assertions.assertEquals("Nuevo Leon", address.getRegion());
        Assertions.assertEquals("Mexico", address.getCountry());
        Assertions.assertEquals("64349", address.getPostalCode());
    }

}