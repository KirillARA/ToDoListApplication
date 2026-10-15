package org.example.service;

import org.example.entity.User;
import org.example.repository.RecordRepository;
import org.example.entity.Record;
import org.example.entity.RecordStatus;
import org.example.entity.dto.RecordsContainerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.example.entity.RecordStatus.ACTIVE;
import static org.example.entity.RecordStatus.DONE;

@Service
@Transactional
public class RecordService {
    private final RecordRepository recordRepository;

    private final UserService userService;

    @Autowired
    public RecordService(RecordRepository recordRepository, UserService userService) {
        this.recordRepository = recordRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public RecordsContainerDto findAllRecords(String filterMode){
        User user = userService.getCurrentUser();
        List<Record> records = user.getRecords().stream()
                .sorted(Comparator.comparing(Record::getId))
                .collect(Collectors.toList());

        int numberOfDoneRecords = (int) records.stream().filter(record -> record.getStatus() == DONE).count();
        int numberOfActiveRecords = (int) records.stream().filter(record -> record.getStatus() == ACTIVE).count();
        if(filterMode == null || filterMode.isBlank()){
            return new RecordsContainerDto(records, numberOfDoneRecords, numberOfActiveRecords, user.getName());
        }

        String filterModeInUpperCase = filterMode.toUpperCase();
        List<String> allowedFilterModes =  Arrays.stream(RecordStatus.values())
                .map(Enum::name)
                .collect(Collectors.toList());
        if (allowedFilterModes.contains(filterModeInUpperCase)){
            List<Record> filteredRecords =  records.stream()
                    .filter(record -> record.getStatus() == RecordStatus.valueOf(filterModeInUpperCase))
                    .collect(Collectors.toList());
            return new RecordsContainerDto(filteredRecords, numberOfDoneRecords, numberOfActiveRecords, user.getName());
        }
        else{
            return new RecordsContainerDto(records, numberOfDoneRecords, numberOfActiveRecords, user.getName());
        }
    }

    public void saveRecord(String title){
        if(title !=null && !title.isBlank()){
            recordRepository.save(new Record(title, userService.getCurrentUser()));
        }
    }

    public void updateRecordStatus(int id, RecordStatus newStatus){
        recordRepository.update(id, newStatus);
    }

    public void deleteRecord(int id){
        recordRepository.deleteById(id);
    }
}
