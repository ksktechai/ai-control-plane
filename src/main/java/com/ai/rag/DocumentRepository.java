package com.ai.rag;

import com.ai.domain.Document;
import java.util.Optional;

/** Repository for document storage and retrieval. */
public interface DocumentRepository {

    /**
     * Saves a document to the repository.
     *
     * @param document The document to save
     * @return The saved document with generated ID
     */
    Document save(Document document);

    /**
     * Finds a document by ID.
     *
     * @param id The document ID
     * @return The document if found
     */
    Optional<Document> findById(String id);

    /** Deletes all documents (for testing). */
    void deleteAll();
}
