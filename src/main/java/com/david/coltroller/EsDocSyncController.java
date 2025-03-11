package com.david.coltroller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import com.david.domain.SysDistrict;
import com.david.dto.es.EsDocAddBatch;
import com.david.dto.es.EsLocation;
import com.david.dto.es.EsSysDistrict;
import com.david.dto.request.EsDocBucketError;
import com.david.dto.request.EsInParams;
import com.david.dto.request.EsRangeParams;
import com.david.dto.request.EsSearchRequest;
import com.david.dto.response.EsResponseBody;
import com.david.service.SysDistrictService;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @authar David
 * @Date 2025/3/5
 * @description
 */
@RestController
@RequestMapping("/es/doc/sync")
public class EsDocSyncController implements ApplicationRunner {

    @Autowired
    private ElasticsearchClient elasticsearchClient;
    @Autowired
    private SysDistrictService sysDistrictService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 同步省市区经纬度数据
//        district();
    }
    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/indexing-bulk.html
     *
     * @param district
     * @return
     * @throws IOException
     */
    @GetMapping("/task/district")
    public ResponseEntity<EsResponseBody> district() throws IOException {

        long id = 0;
        while (true) {
            List<SysDistrict> list = sysDistrictService.lambdaQuery()
                    .gt(SysDistrict::getId, id).orderByAsc(SysDistrict::getId).last("limit 2000").list();

            if (list.isEmpty()) {
                break;
            }
            addBatch(list);
            if ( list.size() < 2000) {
                break;
            }
            id = list.get(list.size()-1).getId();
        }
        return new ResponseEntity<>(null, HttpStatus.OK);

    }

    private void addBatch(List<SysDistrict> list) throws IOException {

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (SysDistrict sysDistrict : list) {
            EsSysDistrict doc = new EsSysDistrict();
            if (sysDistrict.getLat() != null && !sysDistrict.getLat().isEmpty()) {
                doc.setLocation(new EsLocation(Double.parseDouble(sysDistrict.getLng()), Double.parseDouble(sysDistrict.getLat())));
            }
            BeanUtils.copyProperties(sysDistrict, doc, "location");
            br.operations(op -> op
                    .index(idx -> idx
                            .index("sys_district")
                            .document(doc)
                    )
            );
        }
        try {
            elasticsearchClient.bulk(br.build());
        } catch (ElasticsearchException e) {
            System.out.println("ElasticsearchException: " +e);
        }
    }

    /**
     * 组装请求参数
     *
     * @param request
     * @return
     */
    private List<Query> buildQueryList(EsSearchRequest request) {
        List<Query> list = new ArrayList<>();
        if (request.getEqualsParams() != null && !request.getEqualsParams().isEmpty()) {
            for (Map.Entry<String, Object> entry : request.getEqualsParams().entrySet()) {
                // 构建 TermQuery 对象
                Query query = new TermQuery.Builder()
                        .field(entry.getKey())
                        .value(entry.getValue().toString())
                        .build()._toQuery();
                list.add(query);
            }
        }
        if (request.getInParams() != null && !request.getInParams().isEmpty()) {
            for (EsInParams params : request.getInParams()) {
                // 构建 TermsQuery 对象
                Query query = new TermsQuery.Builder()
                        .field(params.getField())
                        .terms(t -> t.value(params.getValueList().stream().map(FieldValue::of).toList()))
                        .build()._toQuery();
                list.add(query);
            }
        }
        if (request.getLikeParams() != null && !request.getLikeParams().isEmpty()) {
            for (Map.Entry<String, String> entry : request.getLikeParams().entrySet()) {
                // 构建 MatchQuery 对象
                Query query = new MatchQuery.Builder()
                        .field(entry.getKey())
                        .query(entry.getValue())
                        .build()._toQuery();
                list.add(query);
            }
        }
        if (request.getRangeParams() != null && !request.getRangeParams().isEmpty()) {
            for (EsRangeParams rangeParam : request.getRangeParams()) {
                if (rangeParam.getFieldType() == 1) {
                    Query query = new NumberRangeQuery.Builder()
                            .field(rangeParam.getField())
                            .gte(Double.valueOf(rangeParam.getGte()))
                            .lte(Double.valueOf(rangeParam.getLte()))
                            .build()._toRangeQuery()._toQuery();
                    list.add(query);
                } else {
                    Query query = new DateRangeQuery.Builder()
                            .field(rangeParam.getField())
                            .gte(rangeParam.getGte())
                            .lte(rangeParam.getLte())
                            .build()._toRangeQuery()._toQuery();
                    list.add(query);
                }
            }
        }
        return list;
    }

}
