package com.example.cctv.controller;

import com.example.cctv.poll.CamService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
public class CamController {
    private CamService camService;

    @GetMapping("/cams")
    public MappingJacksonValue getCams() {

        SimpleBeanPropertyFilter simpleBeanPropertyFilter = SimpleBeanPropertyFilter.serializeAllExcept("sourceDataUrl", "tokenDataUrl");
        FilterProvider filterProvider = new SimpleFilterProvider().addFilter("camFilter", simpleBeanPropertyFilter);

        MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(camService.getCamList());
        mappingJacksonValue.setFilters(filterProvider);

        return mappingJacksonValue;
    }


}
