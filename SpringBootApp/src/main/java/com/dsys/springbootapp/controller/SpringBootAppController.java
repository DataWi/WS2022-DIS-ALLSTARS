package com.dsys.springbootapp.controller;

import com.dsys.springbootapp.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;

@RestController
@RequestMapping("api/v1")
public class SpringBootAppController {

    @Autowired
    private static MessageService messageService = new MessageService();

    //gets file from storage
    @GetMapping("/invoices/{invoice_id}")
    public String getInvoice(@PathVariable int invoice_id){
        return "Invoice is retreived, please wait until download";
    }


    //sends message to get invoice process started
    @GetMapping("/invoice/{customer_id}")
    public void collectInvoice(@PathVariable int customer_id) {
        if(customer_id == Integer.MIN_VALUE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            messageService.sendMessage("spring_app", ""+customer_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}
