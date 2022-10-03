package com.cools.qr;

import ezvcard.property.Telephone;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;
import java.util.Set;

public class PhoneNumbers {

    private final Set<Telephone> phoneNumbers;

    public PhoneNumbers(Set<Telephone> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<Telephone> getPhoneNumbers() {
        return phoneNumbers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PhoneNumbers that = (PhoneNumbers) o;
        return Objects.equals(phoneNumbers, that.phoneNumbers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phoneNumbers);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("phoneNumbers", phoneNumbers)
                .toString();
    }
}
