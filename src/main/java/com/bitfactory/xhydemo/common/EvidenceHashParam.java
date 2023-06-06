package com.bitfactory.xhydemo.common;

import lombok.Data;
import java.util.List;

/**
 * @author admin
 */
@Data
public class EvidenceHashParam {
    private String fileLabel;

    private List<HashInfo> list;

    private Integer attestationChannel = 1;

    @Data
    public static class HashInfo {
        private String filename;
        private String fileHash;
    }
}
