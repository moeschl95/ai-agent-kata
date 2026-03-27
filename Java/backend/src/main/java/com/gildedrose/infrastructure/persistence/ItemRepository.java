package com.gildedrose.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link ItemEntity}. Provides CRUD operations and basic query
 * methods for persisting items.
 */
@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {}
