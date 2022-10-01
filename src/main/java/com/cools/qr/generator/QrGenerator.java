package com.cools.qr.generator;

import com.cools.qr.PhoneNumbers;
import com.cools.qr.converter.AddressConverter;
import com.cools.qr.converter.OrganizationConverter;
import com.cools.qr.converter.PhoneNumbersConverter;
import com.cools.qr.converter.PhoneTypeConverter;
import com.cools.qr.converter.RoleConverter;
import com.cools.qr.util.ImageUtils;
import ezvcard.Ezvcard;
import ezvcard.VCard;
import ezvcard.VCardVersion;
import ezvcard.parameter.TelephoneType;
import ezvcard.property.Address;
import ezvcard.property.Organization;
import ezvcard.property.Role;
import ezvcard.property.StructuredName;
import io.nayuki.qrcodegen.QrCode;
import picocli.CommandLine;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@CommandLine.Command(name = "QrGenerator", version = "QrGenerator 1.0", mixinStandardHelpOptions = true)
public class QrGenerator implements Runnable {

    private static final Logger log = Logger.getLogger(QrGenerator.class.getName());

    @CommandLine.Parameters(index = "0", arity = "1", paramLabel = "first name", description =
            "The contact's first " + "name")
    private String firstName;

    @CommandLine.Parameters(index = "1", arity = "1", paramLabel = "last name", description = "The contact's last name")
    private String lastName;

    @CommandLine.Parameters(index = "2", arity = "1", paramLabel = "email", description = "The contact's email address")
    private String email;



    @CommandLine.Parameters(index = "3", paramLabel = "website url", description = "The contact's website url")
    private String websiteUrl;

    @CommandLine.Parameters(index = "4", arity = "1", paramLabel = "address", description = "The contact's address",
            converter = AddressConverter.class)
    private Address address;

    @CommandLine.Parameters(index = "5", arity = "1", paramLabel = "organization", description = """
                                                                                    The organizations a contact
                                                                                    belongs to, from most to least
                                                                                    specific
                                                                                    """, converter =
            OrganizationConverter.class)
    private Organization organization;

    @CommandLine.Parameters(index = "6", arity = "1", paramLabel = "role", description = "Role within the " +
                                                                            "organization", converter =
            RoleConverter.class)
    private Role role;

    @CommandLine.Parameters(index = "7..*", arity = "1", paramLabel = "phoneNumbers", description = "The contact's " +
                                                                                                    "phone" + " " +
                                                                                                    "numbers")
    private Map<TelephoneType, PhoneNumbers> phoneNumbers;

    public static void main(String... args) {
        QrGenerator qrGenerator = new QrGenerator();
        CommandLine commandLine = new CommandLine(qrGenerator).registerConverter(TelephoneType.class,
                                                                                 new PhoneTypeConverter())
                                                              .registerConverter(PhoneNumbers.class,
                                                                                 new PhoneNumbersConverter());

        int exitCode = commandLine.execute(args);
        System.exit(exitCode);
    }

    @Override
    public void run() {
        QrCode.Ecc errCorLvl = QrCode.Ecc.LOW;

        String vCard = createVCard(firstName, lastName, phoneNumbers, email, websiteUrl, address, organization, role);

        QrCode qr = QrCode.encodeText(vCard, errCorLvl);

        String        filePathName = "target" + "/" + String.join("_", firstName, lastName, Instant.now().toString());
        BufferedImage img          = ImageUtils.toImage(qr, 10, 4); // Convert to bitmap image
        File          imgFile      = new File(filePathName + ".png"); // File path for output

        try {
            ImageIO.write(img, "png", imgFile);                     // Write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write png image file", e);
            throw new RuntimeException(e);
        }

        String svg     = ImageUtils.toSvgString(qr, 1, "#FFFFFF", "#000000");  // Convert to SVG XML code
        File   svgFile = new File(filePathName + ".svg");          // File path for output
        try {
            Files.writeString(svgFile.toPath(), svg); // write image to file
        } catch (IOException e) {
            log.log(Level.SEVERE, "Unable to write svg image file", e);
            throw new RuntimeException(e);
        }
    }

    private String createVCard(String firstName,
                               String lastName,
                               Map<TelephoneType, PhoneNumbers> phoneNumbers,
                               String email,
                               String websiteUrl,
                               Address address,
                               Organization organization,
                               Role role) {
        VCard vcard = new VCard(VCardVersion.V3_0);

        StructuredName n = new StructuredName();
        n.setFamily(lastName);
        n.setGiven(firstName);
        vcard.setStructuredName(n);
        vcard.addProperty(role);

        vcard.setFormattedName(String.join(" ", firstName, lastName));
        vcard.setOrganization(organization);

        for (Map.Entry<TelephoneType, PhoneNumbers> numbers : phoneNumbers.entrySet()) {
            for (String number : numbers.getValue().getPhoneNumbers()) {
                vcard.addTelephoneNumber(number, numbers.getKey());
            }
        }

        vcard.addEmail(email);
        vcard.addUrl(websiteUrl);
        vcard.addAddress(address);

        return Ezvcard.write(vcard).version(VCardVersion.V3_0).go();
    }
}
