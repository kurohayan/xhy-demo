package com.bitfactory.xhydemo.common;

import lombok.Data;

import java.util.List;

/**
 * @author admin
 */
@Data
public class EvidenceFileParam {
    private String fileLabel;

    private List<Long> files;

    private Integer attestationChannel = 1;
}
