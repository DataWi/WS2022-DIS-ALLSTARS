package com.dsys.springbootapp.service;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class MessageService {
    private ConnectionFactory factory = new ConnectionFactory();


    public boolean sendMessage(String to, String message) throws Exception {
        factory.setHost("localhost");
        factory.setPort(30003);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.exchangeDeclare("spring_app", "direct");

            System.out.println(" [x] Springboot app publishing on '" + "spring_app" + "':'" + message + "'");
            channel.basicPublish(to, "dispatch", null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + to + "':'" + message + "'");
        }
        return true;
    }



}
