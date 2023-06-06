package com.bitfactory.xhydemo.example;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.bitfactory.xhydemo.common.EvidenceFileParam;
import com.bitfactory.xhydemo.common.EvidenceHashParam;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 示例程序
 */
public class ApiRequestTest {
    /**
     * 请求地址
     */
    private String uri = "https://test-stamp.bitfactory.cn/api";
    /**
     * accessKey
     */
    private String accessKey = "9d82aeae8c9b4c479715fc2923619472";
    /**
     * SM2私钥
     */
    private String privateKey = "308193020100301306072a8648ce3d020106082a811ccf5501822d047930770201010420ab398da2bb9268c226f4c5908e94841ca6d254a90cf6e66ad848c8e01ee86d33a00a06082a811ccf5501822da144034200049ab45581431741df119e74c8699fd2cb70caeda3c6f05383dd8b4294f3ff5f3c2d7959877584ec884b75a09af99aa69d69c17f6e3018283d0452cbd0debd5262";

    private final String fileNamePattern = ".*filename=\"(.*)\".*";

    /**
     * 查询文件存证详情
     */
    @Test
    public void detail() {
        String apiName = "/evidence/detail";
        HttpRequest httpRequest = createRequestPost(apiName);
        // 构建请求参数
        Map<String ,Object> body = new HashMap<>();
        body.put("attestationId","did:bid:efaE9e45apUbuA87y7Y6zjMTaGfHt7WX");
        httpRequest.body(JSONUtil.toJsonStr(body));
        String result;
        try (HttpResponse httpResponse = httpRequest.execute()) {
            result = httpResponse.body();
        }
        System.out.println(result);
        JSON json = JSONUtil.parse(result);
    }

    /**
     * 查询文件存证列表
     */
    @Test
    public void list() {
        // API path
        String apiName = "/evidence/list";
        HttpRequest httpRequest = createRequestPost(apiName);
        // 构建请求参数
        Map<String ,Object> body = new HashMap<>();
//        body.put("attestationId","");
        httpRequest.body(JSONUtil.toJsonStr(body));
        String result;
        try (HttpResponse httpResponse = httpRequest.execute()) {
            result = httpResponse.body();
        }
        System.out.println(result);
        JSON json = JSONUtil.parse(result);
    }

    /**
     * hash存证(sha256)
     */
    @Test
    public void hash() {
        // API path
        String apiName = "/evidence/hash";
        HttpRequest httpRequest = createRequestPost(apiName);
        // 构建请求参数
        List<EvidenceHashParam.HashInfo> list = new ArrayList<>();
        EvidenceHashParam.HashInfo hashInfo1 = new EvidenceHashParam.HashInfo();
        hashInfo1.setFilename("test1");
        hashInfo1.setFileHash("98df1f1dfb3b1a123c1517912dc70447aa61c6be532ac99de973abb6219e1653");
        list.add(hashInfo1);
        EvidenceHashParam evidenceHashParam = new EvidenceHashParam();
        evidenceHashParam.setFileLabel("标签");
        evidenceHashParam.setList(list);
        httpRequest.body(JSONUtil.toJsonStr(evidenceHashParam));
        String result;
        try (HttpResponse httpResponse = httpRequest.execute()) {
            result = httpResponse.body();
        }
        System.out.println(result);
        JSON json = JSONUtil.parse(result);
    }

    /**
     * 文件存证 第二步存证
     */
    @Test
    public void file() {
        // API path
        String apiName = "/evidence/file";
        HttpRequest httpRequest = createRequestPost(apiName);
        // 构建请求参数
        List<Long> list = new ArrayList<>();
        list.add(1544567382363930624L);
        EvidenceFileParam evidenceFileParam = new EvidenceFileParam();
        evidenceFileParam.setFileLabel("标签");
        evidenceFileParam.setFiles(list);
        httpRequest.body(JSONUtil.toJsonStr(evidenceFileParam));
        String result;
        try (HttpResponse httpResponse = httpRequest.execute()) {
            result = httpResponse.body();
        }
        System.out.println(result);
        JSON json = JSONUtil.parse(result);
    }

    /**
     * 文件存证 第一步上传文件
     */
    @Test
    public void uploadFile() {
        // API path
        String apiName = "/file/upload";
        HttpRequest httpRequest = createRequestPost(apiName);
        httpRequest.form("file",new File("/tmp/背景图.png"));
        httpRequest.form("type","video");

        String result;
        try (HttpResponse httpResponse = httpRequest.execute()) {
            result = httpResponse.body();
        }
        System.out.println(result);
        JSON json = JSONUtil.parse(result);
    }



    /**
     * 下载存证或pdf文件
     * @throws Exception
     */
    @Test
    public void download() throws Exception {
        // API path
        String apiName = "/file/download/1529707935276466176";
        HttpRequest httpRequest = createRequestGet(apiName);

        HttpResponse httpResponse = httpRequest.execute();
        String header = httpResponse.header("Content-Disposition");
        Pattern pattern = Pattern.compile(fileNamePattern);
        Matcher matcher = pattern.matcher(header);
        String fileName = "";
        if (matcher.matches()) {
            fileName = matcher.group(1);
        }
        byte[] bytes = httpResponse.bodyBytes();
        IoUtil.write(Files.newOutputStream(Paths.get("/tmp/" + fileName)),true,bytes);
//        httpResponse.writeBody("/tmp/" + fileName);
    }

    /**
     * 创建post请求
     * @param apiName
     * @return
     */
    private HttpRequest createRequestPost(String apiName) {
        // 构建请求
        HttpRequest httpRequest = HttpUtil.createPost(uri + apiName);
        setHttpRequestHeaders(httpRequest);
        return httpRequest;
    }

    /**
     * 创建get请求
     * @param apiName
     * @return
     */
    private HttpRequest createRequestGet(String apiName) {
        // 构建请求
        HttpRequest httpRequest = HttpUtil.createGet(uri + apiName);
        setHttpRequestHeaders(httpRequest);
        return httpRequest;
    }

    /**
     * 构建请求头
     * @param httpRequest
     * @return
     */
    private HttpRequest setHttpRequestHeaders(HttpRequest httpRequest) {
        // 请求头
        // 使用uuid
        String requestId = IdUtil.simpleUUID();
        String nonce = String.valueOf(System.currentTimeMillis() / 1000);

        //待签名数据 = requestId+accessKey+nonce
        String data = requestId + accessKey + nonce;
        // 开始签名
        SM2 sm2 = new SM2(privateKey,null);
        sm2.setMode(SM2Engine.Mode.C1C2C3);
        sm2.usePlainEncoding();
        // 签名使用Base64编码后得到的值即为请求头中signature字段的值
        String signatureData = Base64.getEncoder().encodeToString(sm2.sign(data.getBytes(StandardCharsets.UTF_8)));
        // 构建请求头
        Map<String ,String> headers = new HashMap<>(8);
        headers.put("request_id", requestId);
        headers.put("access_key", accessKey);
        headers.put("nonce",nonce);
        headers.put("signature",signatureData);
        httpRequest.addHeaders(headers);
        return httpRequest;
    }



}
