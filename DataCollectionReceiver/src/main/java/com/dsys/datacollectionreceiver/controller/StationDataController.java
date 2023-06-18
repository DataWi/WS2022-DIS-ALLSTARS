package com.dsys.datacollectionreceiver.controller;

import com.dsys.datacollectionreceiver.model.CustomerStationData;
import com.dsys.datacollectionreceiver.model.Station;
import com.dsys.datacollectionreceiver.service.MessageService;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class StationDataController {
    private static CustomerStationData customerStationData;
    private static MessageService messageService = new MessageService();


    public  void run() throws IOException, TimeoutException {
        MessageService messageService = new MessageService();
        String[] subscribe = new String[1];
        subscribe[0] = "collection_receiver";
        messageService.listen(subscribe);
    }
    public static void collect(String id, String message) throws Exception {
    if(message.equals("start")) customerStationData = new CustomerStationData(id, new ArrayList<>());
    else if(message.equals("end")) {
        messageService.sendMessage("pdf_service", "start " + customerStationData.getCustomer_id());
        customerStationData.getStations().forEach(station -> {
            String data = String.join(" ", station.getKwh());
            try {
                messageService.sendMessage("pdf_service", station.getId() + " " + data);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        messageService.sendMessage("pdf_service", "end" + customerStationData.getCustomer_id());
    } else {
        customerStationData.getStations().forEach(station -> {
           if ( station.getId().equals(id)) station.getKwh().add(message);
           else {
               Station newStation = new Station(id, new ArrayList<>());
               newStation.getKwh().add(message);
               customerStationData.getStations().add(newStation);
           }
        });
    }
    }
}
