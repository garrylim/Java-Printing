package com.example.demo.Services;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Entity.WeightRecord;
import com.example.demo.Repository.weightRecordRepository;

@Service
@Transactional
public class weightService {

    @Autowired
    private final weightRecordRepository weightRecordRepository;

    public weightService(weightRecordRepository weightRecordRepository) {
        this.weightRecordRepository = weightRecordRepository;
    }

    public List<WeightRecord> getAllWeightRecords() {
        return weightRecordRepository.findAll();
    }

    public void printWeightRecords() {
        List<WeightRecord> weightRecords = weightRecordRepository.findAll();
        
        try {
            // Get the default print service
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            // Create a printable object
            Printable printable = new Printable() {
                @Override
                public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
                    if (pageIndex >= 1) {
                        return Printable.NO_SUCH_PAGE;
                    }

                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

                    // Set font and other formatting properties
                    Font font = new Font("Arial", Font.PLAIN, 12);
                    g2d.setFont(font);
                    
                    int rowHeight = 20;
                    int tableWidth = 300;
                    
                    int x = 10;
                    int y = 20;

                    // Print the table header
                    g2d.drawString("No.", x, y);
                    g2d.drawString("Weight", x + 50, y);
                    g2d.drawString("Date", x + 120, y);
                    y += rowHeight;

                    // Print the weight records
                    double totalWeight = 0.0; // Variable to hold total weight
                    for (int i = 0; i < weightRecords.size(); i++) {
                        WeightRecord record = weightRecords.get(i);
                        g2d.drawString(String.valueOf(i + 1), x, y);
                        g2d.drawString(String.valueOf(record.getWeight()), x + 50, y);
                        g2d.drawString(record.getTimestamp().toLocalDate().toString(), x + 120, y);
                        y += rowHeight;

                        totalWeight += record.getWeight(); // Add weight to totalWeight
                    }

                    // Print the footer
                    int totalRecords = weightRecords.size();
                    g2d.drawString("Total Number of Records: " + totalRecords, x, y);
                    y += rowHeight;
                    g2d.drawString("Total Weight: " + totalWeight + " kg", x, y);

                    return Printable.PAGE_EXISTS;
                }
            };

            // Create a printer job
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintService(defaultPrintService);
            printerJob.setPrintable(printable);

            // Trigger the print job
            printerJob.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }
}
