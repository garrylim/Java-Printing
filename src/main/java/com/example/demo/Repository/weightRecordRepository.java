package com.example.demo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.Entity.WeightRecord;

@Repository
public interface weightRecordRepository extends JpaRepository<WeightRecord, Long> {

}
