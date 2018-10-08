package uz.oltinolma.producer.elasticsearch.search;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public abstract class AbstractSearchHelper {
    @Autowired
    protected TransportClient client;

    protected SearchRequest searchRequest(QueryBuilder queryBuilder, String... indices) {
        SearchSourceBuilder search = new SearchSourceBuilder();
        search.query(queryBuilder);
        SearchRequest request = new SearchRequest();
        request.indices(indices);
        request.source(search);
        return request;
    }

    protected QueryBuilder fuzzyQuery(String field, String term) {
        FuzzyQueryBuilder fuzzy = QueryBuilders.fuzzyQuery(field, term);
        if (term.length() <= 3)
            fuzzy.fuzziness(Fuzziness.ONE);

        return fuzzy;
    }


    protected List<SearchResult> getTopTen(MultiSearchResponse response) {
        List<SearchResult> results = new ArrayList<>();
        response.iterator().forEachRemaining(item -> {
            if (!item.isFailure()) {
                item.getResponse().getHits().iterator().forEachRemaining(hit -> results.add(new SearchResult(hit)));
            }
        });

        results.sort((searchResult1, searchResult2) -> (int) (searchResult2.getScore() - searchResult1.getScore()));
        return results.stream().filter(sr -> sr.getTaxonomy() != null).limit(10).collect(Collectors.toList());
    }

    protected List<SearchResult> getTopTen(SearchResponse response) {
        List<SearchResult> results = new ArrayList<>();
        response.getHits().iterator().forEachRemaining(hit -> results.add(new SearchResult(hit)));

        results.sort((searchResult1, searchResult2) -> (int) (searchResult2.getScore() - searchResult1.getScore()));
        return results.stream().filter(sr -> sr.getTaxonomy() != null).limit(10).collect(Collectors.toList());
    }
}