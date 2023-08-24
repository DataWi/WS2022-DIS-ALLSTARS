package com.dsys.springbootapp.controller;

import com.dsys.springbootapp.service.MessageService;
import com.dsys.springbootapp.controller.PostBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
@RequestMapping("api/v1")
public class SpringBootAppController {

    @Autowired
    private static MessageService messageService = new MessageService();


    //sends message to get invoice process started
    @PostMapping("/invoice")
    @CrossOrigin(origins = "", allowedHeaders = "")
    public void collectInvoice(@RequestBody PostBody customer) {
        int customer_id = customer.getCustomer_id();

        if(customer_id == Integer.MIN_VALUE){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        try {
            messageService.sendMessage("spring_app", ""+customer_id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //gets invoice file from storage
    @GetMapping("/invoices/{customer_id}")
    @CrossOrigin(origins = "", allowedHeaders = "*")
    public ResponseEntity getInvoice(@PathVariable int customer_id) {
        try{
            Path path = Paths.get("../" + "Invoice" + customer_id + ".pdf");
            Resource invoice = new UrlResource(path.toUri());

            if (!invoice.exists()) {
                throw new FileNotFoundException("Invoice not found");
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + invoice.getFilename() + "\"")
                    .body(Base64.getEncoder().encode(invoice.getContentAsByteArray()));


        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }


    }

}