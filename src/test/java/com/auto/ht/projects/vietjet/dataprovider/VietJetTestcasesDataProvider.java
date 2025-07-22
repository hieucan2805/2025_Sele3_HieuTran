package com.auto.ht.projects.vietjet.dataprovider;

import com.auto.ht.projects.vietjet.enums.FlightType;
import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.models.PassengerModel;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.auto.ht.utils.CSVDataProvider.readCSVData;

public class VietJetTestcasesDataProvider {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(VietJetTestcasesDataProvider.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Base path for all test data files
    private static final String TEST_DATA_BASE_PATH = "src/test/resources/testdatas/vietjet/";

    @DataProvider(name = "testcase001Data")
    public Object[][] Testcase001Data() {
        // Load data from specific CSV file for TestCase001
        String filePath = TEST_DATA_BASE_PATH + "testcase001/flight_data.csv";
        List<BookingInformationModel> bookingData = getBookingInformationFromCSV(filePath);

        // Convert List to Object[][] for DataProvider
        Object[][] data = new Object[bookingData.size()][1];
        for (int i = 0; i < bookingData.size(); i++) {
            data[i][0] = bookingData.get(i);
        }
        log.info("Test case 001 data loaded from: {}", filePath);
        return data;
    }

    @DataProvider(name = "testcase002Data")
    public Object[][] Testcase002Data() {
        // Load data from specific CSV file for TestCase002
        String filePath = TEST_DATA_BASE_PATH + "testcase002/flight_data.csv";
        List<BookingInformationModel> bookingData = getBookingInformationFromCSV(filePath);

        if (bookingData.isEmpty()) {
            throw new RuntimeException("No test data found in " + filePath);
        }

        BookingInformationModel tc002Data = bookingData.get(0);
        log.info("Test case 002 data loaded from: {}", filePath);
        return new Object[][]{{tc002Data}};
    }

    /**
     * Convert CSV data to BookingInformationModel objects
     */
    public static List<BookingInformationModel> getBookingInformationFromCSV(String filePath) {
        List<Map<String, String>> csvData = readCSVData(filePath);
        List<BookingInformationModel> bookings = new ArrayList<>();

        for (Map<String, String> row : csvData) {
            BookingInformationModel booking = new BookingInformationModel();

            String typeStr = row.getOrDefault("Type", "return");
            booking.setType(FlightType.fromName(typeStr)); // Use fromName method to convert string to enum

            booking.setFrom(row.getOrDefault("From", ""));
            booking.setTo(row.getOrDefault("To", ""));
            
            // Parse date string to LocalDate with proper formatting
            String departureDateStr = row.getOrDefault("DepartureDate", "");
            if (departureDateStr.isEmpty()) {
                booking.setDepartureDate(null);
            } else {
                booking.setDepartureDate(LocalDate.parse(departureDateStr, DATE_FORMATTER));
            }
            
            booking.setDuration(row.getOrDefault("Duration", "today"));
            booking.setRange(row.getOrDefault("Range", ""));
            PassengerModel passenger = new PassengerModel();
            passenger.setAdults(Integer.parseInt(row.getOrDefault("Adults", "1")));
            passenger.setChild(Integer.parseInt(row.getOrDefault("Children", "0")));
            passenger.setBaby(Integer.parseInt(row.getOrDefault("Baby", "0")));
            booking.setPassenger(passenger);

            bookings.add(booking);
        }

        return bookings;
    }
}
