package com.dsys.springbootapp.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SpringBootAppController.class)
class SpringBootAppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void postCollectInvoice() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(post("http://localhost:8080/api/v1/invoice")
                        .content("2").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testGetInvoice() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("http://localhost:8080/api/v1/invoices/{customer_id}", 2)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void testGetInvoice_ThrowsFileNotFoundException() throws Exception {
        // Setup
        // Run the test
        final MockHttpServletResponse response = mockMvc.perform(get("http://localhost:8080/api/v1/invoices/{customer_id}", "d")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }
}
