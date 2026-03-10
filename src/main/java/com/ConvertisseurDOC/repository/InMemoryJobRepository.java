package com.ConvertisseurDOC.repository;


import com.ConvertisseurDOC.model.ConversionJob;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryJobRepository implements JobRepository {

    private final ConcurrentHashMap<String, ConversionJob> store =
            new ConcurrentHashMap<String, ConversionJob>();

    @Override
    public void save(ConversionJob job) {
        if (job == null || job.getId() == null) {
            throw new IllegalArgumentException("Job ou Job ID invalide.");
        }
        store.put(job.getId(), job);
    }

    @Override
    public ConversionJob findById(String id) {
        if (id == null) return null;
        return store.get(id);  // ✅ maintenant ça retourne bien un ConversionJob
    }

    @Override
    public boolean existsById(String id) {
        return id != null && store.containsKey(id);
    }

    public void deleteById(String id) {
        if (id != null) store.remove(id);
    }
}