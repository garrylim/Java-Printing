package com.example.demo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.Component.PrintReceipt;
import com.example.demo.Entity.WeightRecord;
import com.example.demo.Services.WeightRecordService;
import com.example.demo.Services.weightService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class WeightController {

    @Autowired
    private weightService weightService;

    @Autowired
    private WeightRecordService weightRecordService;

    @Autowired
    private PrintReceipt printReceipt;

    @GetMapping("/")
    public String getAllWeightRecords(Model model) {
        List<WeightRecord> weightRecords = weightService.getAllWeightRecords();
        model.addAttribute("weightRecords", weightRecords);
        return "weight-records";
    }

    @GetMapping("/print")
    public String printReceipt() {
        printReceipt.printReceipt();
        return "Receipt printed successfully";
    }

    @GetMapping("/reset-and-save")
    public ResponseEntity<String> resetDataAndSaveToFile() {
        // Reset the data in the database
        weightRecordService.resetData();

        // Save the data to a .txt file
        List<WeightRecord> weightRecords = weightService.getAllWeightRecords();
        String fileContent = "ID, Timestamp, Weight\n";
        for (WeightRecord record : weightRecords) {
            fileContent += record.getId() + ", " + record.getTimestamp() + ", " + record.getWeight() + " Kg\n";
        }

        try {
            // Change the file path as per your requirement
            Files.write(Paths.get("C:\\Users\\Non-Woven\\Desktop\\data.txt"), fileContent.getBytes());
            return ResponseEntity.ok("Data has been reset and saved to file.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while saving data to file.");
        }
    }

    @PostMapping("/delete-weight")
    public ResponseEntity<String> deleteWeightRecord(@RequestParam Long id) {
        weightRecordService.deleteWeightRecordById(id);
        return ResponseEntity.ok("Record deleted successfully.");
    }
}

