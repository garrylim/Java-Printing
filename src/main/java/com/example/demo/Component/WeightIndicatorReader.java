package com.example.demo.Component;

import com.fazecast.jSerialComm.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.print.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import javax.print.PrintService;

import com.example.demo.Entity.WeightRecord;
import com.example.demo.Repository.weightRecordRepository;

@Component
public class WeightIndicatorReader {

    private static final String COM_PORT_NAME = "COM3"; // Replace with your desired COM port name
    private static final Logger logger = LoggerFactory.getLogger(WeightIndicatorReader.class);

    private final weightRecordRepository weightRecordRepository;

    @Autowired
    public WeightIndicatorReader(weightRecordRepository weightRecordRepository) {
        this.weightRecordRepository = weightRecordRepository;
    }

    public void readDataFromWeightIndicator() {
        SerialPort[] serialPorts = SerialPort.getCommPorts();
        SerialPort selectedPort = null;
        for (SerialPort port : serialPorts) {
            if (port.getSystemPortName().equals(COM_PORT_NAME)) {
                selectedPort = port;
                break;
            }
        }
        if (selectedPort == null) {
            logger.error("Specified serial port not found: {}", COM_PORT_NAME);
            return;
        }
        selectedPort.openPort();

        selectedPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);

        final SerialPort serialPort = selectedPort;
        Thread readThread = new Thread(() -> {
            try {
                logger.info("Connected to serial port: {}", COM_PORT_NAME);
                while (true) {
                    if (serialPort.bytesAvailable() >= 22) {
                        byte[] buffer = new byte[22];
                        int bytesRead = serialPort.readBytes(buffer, buffer.length);

                        // Extract the data bytes (10-17) as ASCII characters
                        byte[] dataBytes = new byte[8];
                        System.arraycopy(buffer, 10, dataBytes, 0, 8);

                        // Convert the ASCII characters to a string
                        String data = new String(dataBytes, StandardCharsets.US_ASCII);

                        // Clean the data by removing unwanted characters or whitespace
                        data = data.trim().replace(",", "");

                        logger.info("Received data: {}", data);

                        // Process the cleaned data
                        double weight = Double.parseDouble(data);

                        // Create a new WeightRecord entity
                        WeightRecord weightRecord = new WeightRecord(LocalDateTime.now(), weight);

                        // Save the WeightRecord to the database
                        weightRecordRepository.save(weightRecord);

                        // Format the weight and time data for the label
                        String formattedWeight = String.format("%.2f", weight);
                        String formattedTime = LocalDateTime.now().toString();

                        // Print the label
                        printLabel(formattedWeight, formattedTime);
                    }
                    Thread.sleep(100); // Adjust the delay as needed
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        readThread.start();
    }

    private void printLabel(String weight, String time) {
        try {
            // Create a Printable object for label printing
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex == 0) {
                        Graphics2D g2d = (Graphics2D) graphics;
                        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
    
                        // Set font and other styling properties
                        Font font = new Font("Arial Narrow", Font.BOLD, 15);
                        g2d.setFont(font);
    
                        // Calculate the position for drawing the text
                        int x = 10; // Position at the left
                        int y = 20; // Position at the top
    
                        // Draw the weight and date data
                        g2d.drawString("Wgt:" + weight, x, y);
                        g2d.drawString(time.split("T")[0], x, y + 20); // Extracting the date part
    
                        return Printable.PAGE_EXISTS;
                    } else {
                        return Printable.NO_SUCH_PAGE;
                    }
                }
            };
    
            // Get the default printer
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            PrintService defaultPrintService = printerJob.getPrintService();
            if (defaultPrintService == null) {
                logger.error("No default print service found");
                return;
            }
    
            // Set the printable object and print attributes
            printerJob.setPrintService(defaultPrintService);
            printerJob.setPrintable(printable);
    
            // Create a PageFormat with the desired paper size
            Paper paper = new Paper();
            double paperWidth = 3.950; // 3.937 inches width
            double paperHeight = 2.756; // 2.756 inches height
            double inchToMm = 25.4;
            paper.setSize(paperWidth * inchToMm, paperHeight * inchToMm);
            PageFormat pageFormat = new PageFormat();
            pageFormat.setPaper(paper);
    
            // Set the PageFormat to the printer job
            printerJob.setPrintable(printable, pageFormat);
    
            // Print the label
            printerJob.print();
        } catch (PrinterException e) {
            logger.error("Error occurred during printing");
            e.printStackTrace();
        }
    }
    
    
}
