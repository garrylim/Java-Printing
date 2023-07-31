package com.example.demo.Component;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.Services.WeightRecordService;
import com.example.demo.Entity.WeightRecord;

@Component
public class PrintReceipt {

    private final WeightRecordService weightRecordService;

    @Autowired
    public PrintReceipt(WeightRecordService weightRecordService) {
        this.weightRecordService = weightRecordService;
    }

    public void printReceipt() {
        try {
            // Get the Bixolon printer service
            PrintService bixolonPrintService = findBixolonPrintService();

            if (bixolonPrintService == null) {
                System.out.println("Bixolon printer is not found. Please make sure it is installed and configured properly.");
                return;
            }

            // Fetch weight records from the service
            List<WeightRecord> weightRecords = weightRecordService.getAllWeightRecords();

            // Create a printable object
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex >= 1) {
                        return Printable.NO_SUCH_PAGE;
                    }

                    Graphics2D g2d = (Graphics2D) graphics;

                    // Set font and other formatting properties
                    Font font = new Font("Arial Narrow", Font.PLAIN, 12);
                    g2d.setFont(font);

                    // Calculate the position for drawing the text
                    int x = 0; // Adjust this value to align the text to the left
                    int y = 20;

                    // Print the date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String printedDate = dateFormat.format(new Date());
                    g2d.drawString("Date: " + printedDate, x, y);
                    y += 20;

                    // Draw the table header
                    g2d.drawString("No.", x, y);
                    g2d.drawString("Weight", x + 30, y);
                    y += 15;

                    // Draw the table rows
                    int recordCount = 1;
                    for (WeightRecord record : weightRecords) {
                        g2d.drawString(String.valueOf(recordCount), x, y);
                        g2d.drawString(String.format("%.1f Kg", record.getWeight()), x + 30, y);
                        y += 15;
                        recordCount++;
                    }

                    // Draw the table footer
                    int totalRecords = weightRecords.size();
                    double totalWeight = weightRecordService.calculateTotalWeight(weightRecords);
                    g2d.drawString("------------------------------------", x, y);
                    y += 15;
                    g2d.drawString("Total Roll: " + totalRecords, x, y);
                    y += 15;
                    g2d.drawString("Total Weight: " + String.format("%.1f kg", totalWeight), x, y);
                    y += 15;
                    g2d.drawString("------------------------------------", x, y);

                    return Printable.PAGE_EXISTS;

                }
            };

            // Create a printer job
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintService(bixolonPrintService);
            printerJob.setPrintable(printable);

            // Set the paper size
            Paper paper = new Paper();
            double paperWidth = 200.0; // 7.6cm width
            double paperHeight = 3276.0; // Maximum height
            paper.setSize(paperWidth, paperHeight);
            paper.setImageableArea(0, 0, paperWidth, paperHeight);
            PageFormat pageFormat = printerJob.defaultPage();
            pageFormat.setPaper(paper);
            printerJob.setPrintable(printable, pageFormat);

            // Trigger the print job
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private PrintService findBixolonPrintService() {
        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        for (PrintService printService : printServices) {
            if (printService.getName().equals("BIXOLON SRP-275II")) {
                return printService;
            }
        }
        return null;
    }
}
