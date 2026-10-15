package org.example.controller.secured;

import org.example.entity.User;
import org.example.entity.dto.RecordsContainerDto;
import org.example.service.RecordService;
import org.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static org.example.entity.RecordStatus.DONE;

@Controller
@RequestMapping("/account")
public class PrivateAccountController {
    private final RecordService recordService;
    private final UserService userService;

    @Autowired
    public PrivateAccountController(RecordService recordService, UserService userService) {
        this.recordService = recordService;
        this.userService = userService;
    }

    @GetMapping
    public String getMainPage(Model model, @RequestParam(value = "filter", required = false) String filterMode){
        RecordsContainerDto container =  recordService.findAllRecords(filterMode);
        model.addAttribute("userName", container.getUserName());
        model.addAttribute("records", container.getRecords());
        model.addAttribute("numberOfDoneRecords", container.getNumberOfDoneRecords());
        model.addAttribute("numberOfActiveRecords", container.getNumberOfActiveRecords());
        return "private/account-page";
    }

    @PostMapping( "/add-record")
    public String addRecord(@RequestParam("title") String title){
        recordService.saveRecord(title);
        return "redirect:/account";
    }

    @PostMapping( "/make-record-done")
    public String makeRecordDone(@RequestParam("id") int id,
                                 @RequestParam(name = "filter", required = false) String filterMode){
        recordService.updateRecordStatus(id, DONE);
        return "redirect:/account" + (filterMode != null && !filterMode.isBlank() ? "?filter=" + filterMode : "");
    }

    @PostMapping( "/delete-record")
    public String deleteRecord(@RequestParam("id") int id,
                               @RequestParam(name = "filter", required = false) String filterMode){
        recordService.deleteRecord(id);
        return "redirect:/account" + (filterMode != null && !filterMode.isBlank() ? "?filter=" + filterMode : "");
    }


}
