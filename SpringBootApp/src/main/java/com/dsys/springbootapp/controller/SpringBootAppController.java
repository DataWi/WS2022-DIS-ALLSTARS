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
        //was muss hier sonst noch passieren? anbindung an invoice db nehme ich an
    }


    //sends message to get invoice process started
    @PostMapping("/invoice/{customer_id}")
    public void collectInvoice(int customer_id) throws SQLException {
        if(customer_id == Integer.MIN_VALUE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            messageService.sendMessage("data_collector", "" +customer_id, ""+customer_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }





}