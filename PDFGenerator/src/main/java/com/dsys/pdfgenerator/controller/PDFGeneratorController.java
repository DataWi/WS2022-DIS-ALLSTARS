package com.dsys.pdfgenerator.controller;

import com.dsys.pdfgenerator.model.Customer;
import com.dsys.pdfgenerator.model.Print;
import com.dsys.pdfgenerator.model.StationBillingList;
import com.dsys.pdfgenerator.service.DatabaseService;
import com.dsys.pdfgenerator.service.MessageService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class PDFGeneratorController {
    private static MessageService messageService = new MessageService();
    private static DatabaseService databaseService = new DatabaseService();
    private static Print print = null;
    public static void run() throws IOException, TimeoutException {
        String[] subscribe = new String[1];
        subscribe[0] = "pdf_service";
        messageService.listen(subscribe);
    }

    public static void print(String[] message) throws SQLException, DocumentException, FileNotFoundException {
        Customer customer;

        if (message[0].equals("start")) {
            customer = databaseService.getCustomer(message[1]);
            print = new Print(customer, new ArrayList<>());
        }
        else if (message[0].equals("end") ) generate(print);
        else {
            StationBillingList billings = new StationBillingList(message[0], new ArrayList<>());
            for(int i = 1; i < message.length; i++) billings.getKwh().add(message[i]);
            print.getBillings().add(billings);
        }

    }

    public static void generate(Print print) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("Invoice " + print.getCustomer().getLast_name()+ ".pdf"));
        String spacing = "        ";

        document.open();
        Font header = FontFactory.getFont(FontFactory.COURIER, 32, BaseColor.BLACK);
        Font subHeader = FontFactory.getFont(FontFactory.COURIER, 25, BaseColor.BLACK);
        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        String line = "Invoice for " + print.getCustomer().getFirst_name() + " " + print.getCustomer().getLast_name();
        Chunk chunk = new Chunk(spacing + spacing + line, header);

        document.add(chunk);

        print.getBillings().forEach(billing -> {
           Chunk billingHeading = new Chunk("Billings for station " + billing.getStation_id(), subHeader);
            try {
                document.add(billingHeading);
            } catch (DocumentException e) {
                throw new RuntimeException(e);
            }

            billing.getKwh().forEach(kwh -> {
                Chunk amount = new Chunk(spacing + kwh, font);
                try {
                    document.add(amount);
                } catch (DocumentException e) {
                    throw new RuntimeException(e);
                }
            });

        });


        document.close();


    }
}
