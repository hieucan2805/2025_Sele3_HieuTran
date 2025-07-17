package com.auto.ht.projects.vietjet.dataprovider;

import com.auto.ht.projects.vietjet.models.BookingInformationModel;
import com.auto.ht.projects.vietjet.models.PassengerModel;
import org.testng.annotations.DataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.auto.ht.utils.CSVDataProvider.readCSVData;

public class TestCase001Provider {

    @DataProvider(name = "flightSearchDataProvider")
    public Object[][] flightSearchDataProvider() {
        // Load data from CSV file
        String filePath = "src/test/resources/testdatas/vietjet/flight_data.csv";
        List<BookingInformationModel> bookingData = getBookingInformationFromCSV(filePath);
        // Convert List to Object[][] for DataProvider
        Object[][] data = new Object[bookingData.size()][1];
        for (int i = 0; i < bookingData.size(); i++) {
            data[i][0] = bookingData.get(i);
        }
        return data;
    }

    /**
     * Convert CSV data to BookingInformationModel objects
     */
    public static List<BookingInformationModel> getBookingInformationFromCSV(String filePath) {
        List<Map<String, String>> csvData = readCSVData(filePath);
        List<BookingInformationModel> bookings = new ArrayList<>();

        for (Map<String, String> row : csvData) {
            BookingInformationModel booking = new BookingInformationModel();

            booking.setType(row.getOrDefault("Type", "Return")); // Default to Return if not specified
            booking.setFrom(row.getOrDefault("From", ""));
            booking.setTo(row.getOrDefault("To", ""));
            booking.setDepartureDate(row.getOrDefault("DepartureDate", ""));
            booking.setDuration(row.getOrDefault("Duration", ""));

            PassengerModel passenger = new PassengerModel();
            passenger.setAdults(row.getOrDefault("Adults", "1"));
            passenger.setChild(row.getOrDefault("Children", "0"));
            passenger.setBaby(row.getOrDefault("Baby", "0"));
            booking.setPassenger(passenger);

            bookings.add(booking);
        }

        return bookings;
    }
}
