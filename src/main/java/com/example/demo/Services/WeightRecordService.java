package com.example.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.demo.Entity.WeightRecord;
import com.example.demo.Repository.weightRecordRepository;
import java.util.List;

@Service
public class WeightRecordService {
    
    private final weightRecordRepository weightRecordRepository;
    
    @Autowired
    public WeightRecordService(weightRecordRepository weightRecordRepository) {
        this.weightRecordRepository = weightRecordRepository;
    }
    
    public List<WeightRecord> getAllWeightRecords() {
        return weightRecordRepository.findAll();
    }
    
    public double calculateTotalWeight(List<WeightRecord> weightRecords) {
        double totalWeight = 0.0;
        for (WeightRecord record : weightRecords) {
            totalWeight += record.getWeight();
        }
        return totalWeight;
    }

    public void resetData() {
        // Delete all records in the database
        weightRecordRepository.deleteAll();
    }

    public void deleteWeightRecordById(Long id) {
        weightRecordRepository.deleteById(id);
    }
    
}
