package com.david.coltroller;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.mget.MultiGetResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.david.dto.es.EsDocAdd;
import com.david.dto.es.EsDocAddBatch;
import com.david.dto.request.*;
import com.david.dto.response.EsResponseBody;
import com.david.dto.response.EsSearchResult;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/es/doc")
public class EsDocController {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    /**
     * 根据es的objectId获取数据
     * @param index
     * @param id
     * @return
     * @throws IOException
     */
    @GetMapping("/get")
    public ResponseEntity<EsResponseBody> get(@RequestParam("index") String index, @RequestParam("id") String id) throws IOException {

        GetResponse<ObjectNode> response = elasticsearchClient.get(g -> g
                        .index(index)
                        .id(id),
                ObjectNode.class
        );
        if (response.found()) {
            EsResponseBody responseBody = new EsResponseBody(0, response.source(), null);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
        EsResponseBody responseBody = new EsResponseBody(-1, null, "not found");
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 根据es的多个objectId获取数据
     * @param params
     * @return
     * @throws IOException
     */
    @PostMapping("/getBatch")
    public ResponseEntity<EsResponseBody> getBatch(@RequestBody EsGetBatchParams params) throws IOException {

        MgetResponse<ObjectNode> response = elasticsearchClient.mget(g -> g
                        .index(params.getIndex())
                        .ids(params.getIdList()),
                ObjectNode.class
        );
        List<ObjectNode> list = new ArrayList<>();
        for (MultiGetResponseItem<ObjectNode> doc : response.docs()) {
            if (doc.isResult()) {
                list.add(doc.result().source());
            }
            if (doc.isFailure()) {
                System.out.println("isFailure doc" + doc.toString());
            }
        }
        EsResponseBody responseBody = new EsResponseBody(0, list, null);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 查询分页
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/search")
    public ResponseEntity<EsResponseBody> search(@RequestBody EsSearchRequest request) throws IOException {
        // 构造查询过滤条件
        List<Query> list = buildQueryList(request);
        Query query = null;
        if (request.getLikeParams() != null && !request.getLikeParams().isEmpty()) {
            // 使用分值查询，分高的排前面
            query = new BoolQuery.Builder().must(list).build()._toQuery();
        } else {
            // 使用filter不算分查询
            query = new BoolQuery.Builder().filter(list).build()._toQuery();
        }
        // 创建 SearchRequest 并指定索引名称
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index(request.getIndexList())
                .query(query)
                .trackTotalHits(tth -> tth.enabled(true))
                .from((request.getPageIndex() - 1) * request.getPageSize())
                .size(request.getPageSize())
                .build();

        // 打印查询语句，方便排查问题
        System.out.println("search query: " + searchRequest.toString());

        // 执行搜索请求， 第二个参数可以换成自己的返回值对象
        SearchResponse<ObjectNode> response = elasticsearchClient.search(searchRequest, ObjectNode.class);
        List<ObjectNode> collect = response.hits().hits().stream().map(Hit::source).toList();
        EsSearchResult result = new EsSearchResult();
        result.setPageIndex(request.getPageIndex());
        result.setPageSize(request.getPageSize());
        result.setTotal(response.hits().total().value());
        result.setList(collect);
        EsResponseBody responseBody = new EsResponseBody(0, result, null);

        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * 数据分析统计
     * 统计请求的类来源和平台
     * @param request
     * @return
     * @throws IOException
     */
    @PostMapping("/aggs")
    public ResponseEntity<EsResponseBody> aggs(@RequestBody EsSearchRequest request) throws IOException {

        // 构造查询过滤条件
        List<Query> list = buildQueryList(request);

        Query query = new BoolQuery.Builder().filter(list).build()._toQuery();

        // 创建 SearchRequest 并指定索引名称和其他参数
        SearchRequest searchRequest = new SearchRequest.Builder()
                .index("ssp_ad_union_log")
                .trackTotalHits(tth -> tth.enabled(true))
                .from(0)
                .size(1)
                .query(query)
                .aggregations("provinceName_bucket",
                        // 一级聚合，每个省份的流量数据，size控制返回数量
                        a -> a.terms(t -> t.field("provinceName").size(30))
                                // 二级聚合，不同省份下多个维度的指标聚合数据，如手机品牌、来源平台、网络类型、sdk版本等
                                .aggregations("bizType_bucket", aa -> aa.terms(ts -> ts.field("bizType").size(10)))
                                .aggregations("network_bucket", aa -> aa.terms(ts -> ts.field("network").size(10)))
                                .aggregations("sdkVersion_bucket", aa -> aa.terms(t3 -> t3.field("sdkVersion").size(10)))
                                .aggregations("platformName_bucket", aa -> aa.terms(t4 -> t4.field("platformName").size(10)))
                                .aggregations("phoneBrandName_bucket", aa -> aa.terms(t5 -> t5.field("phoneBrandName").size(20)))
                                // 带条件二级聚合
                                .aggregations("ecpmAvg_bucket", aa -> aa.filter(f6 -> f6.term(c -> c.field("bizType").value(2))).aggregations("ecpmAvg_bucket", ss -> ss.avg(avg -> avg.field("ecpm"))))
                )
                .build();

        // 打印查询语句，方便排查问题
        System.out.println("search query: " + searchRequest.toString());

        // 执行搜索请求
        SearchResponse<ObjectNode> response = elasticsearchClient.search(searchRequest, ObjectNode.class);
        // 处理结果
        System.out.println(response.aggregations().toString());
        List<StringTermsBucket> buckets = response.aggregations()
                .get("cityName_bucket")
                .sterms()
                .buckets()
                .array();
        EsResponseBody responseBody = new EsResponseBody(0, response.aggregations().toString(), null);
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/indexing.html
     *
     * @param add
     * @return
     * @throws IOException
     */
    @PostMapping("/add")
    public ResponseEntity<EsResponseBody> add(@RequestBody EsDocAdd add) throws IOException {
        try {
            IndexResponse response = elasticsearchClient.index(i -> i
                    .index(add.getIndex())
                    // 不设置id，默认使用es生成的
                    // .id(product.getSku())
                    .document(add.getDoc())
            );
            EsResponseBody responseBody = new EsResponseBody(0, response.id(), null);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ElasticsearchException e) {
            EsResponseBody responseBody = new EsResponseBody(-1, null, e.error().toString());
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        }
    }

    /**
     * https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/indexing-bulk.html
     *
     * @param add
     * @return
     * @throws IOException
     */
    @PostMapping("/addBatch")
    public ResponseEntity<EsResponseBody> addBatch(@RequestBody EsDocAddBatch add) throws IOException {

        BulkRequest.Builder br = new BulkRequest.Builder();
        for (ObjectNode doc : add.getDocs()) {
            br.operations(op -> op
                    .index(idx -> idx
                            .index(add.getIndex())
                            .document(doc)
                    )
            );
        }
        try {
            BulkResponse result = elasticsearchClient.bulk(br.build());
            if (result.errors()) {
                List<EsDocBucketError> list = new ArrayList<>();
                int i = 0;
                for (BulkResponseItem item : result.items()) {
                    if (item.error() != null) {
                        list.add(new EsDocBucketError(item.error().reason(), add.getDocs().get(i)));
                    }
                    i++;
                }
                EsResponseBody responseBody = new EsResponseBody(-1, list, "失败");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            }
            EsResponseBody responseBody = new EsResponseBody(0, null, null);
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
        } catch (ElasticsearchException e) {
            EsResponseBody responseBody = new EsResponseBody(-1, null, e.error().toString());
            return new ResponseEntity<>(responseBody, HttpStatus.OK);
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
