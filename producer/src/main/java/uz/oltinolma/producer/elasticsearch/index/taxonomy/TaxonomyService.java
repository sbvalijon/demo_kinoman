package uz.oltinolma.producer.elasticsearch.index.taxonomy;

import org.elasticsearch.client.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.oltinolma.producer.elasticsearch.index.tools.IndexingTools;

import java.util.List;

@Service
public class TaxonomyService {
    @Autowired
    private TaxonomyPgRepository postgres;
    @Autowired
    private TaxonomyElasticsearchRepository elasticsearch;
    @Autowired
    private Client client;


    public void index(Taxonomy taxonomy) {
        elasticsearch.save(taxonomy);
    }

    public void indexAll(List<Taxonomy> taxonomies) {
        elasticsearch.saveAll(taxonomies);
    }

    public void indexAll() {
        prepareIndex();
        elasticsearch.saveAll(getAllTaxonomiesFromPg());
    }

    public List<Taxonomy> getAllTaxonomiesFromPg() {
        return postgres.getAllTaxonomies();
    }

    private void prepareIndex(){
        try {
            IndexingTools indexingTools = new IndexingTools()
                    .setClient(client)
                    .setAnalyzerName("autocomplete")
                    .setIndexName("taxonomy_index")
                    .setType("taxonomy")
                    .addField("name");
            indexingTools.deleteIndexIfExists();
            indexingTools.indexWithEdge_ngramAnalyzer();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
