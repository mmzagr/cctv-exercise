package com.example.cctv.data;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@JsonFilter("camFilter")
public class CameraInfo {
    private int id;
    private String sourceDataUrl;
    private String tokenDataUrl;
    @JsonUnwrapped
    private SourceData sourceData;
    @JsonUnwrapped
    private TokenData tokenData;
}
