package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Component.PrintReceipt;

@Service
public class receiptService {
    
    private final PrintReceipt printReceipt;
    
    @Autowired
    public receiptService(PrintReceipt printReceipt) {
        this.printReceipt = printReceipt;
    }
    
    public void printReceipt() {
        printReceipt.printReceipt();
    }
}

