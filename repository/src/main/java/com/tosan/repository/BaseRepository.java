package com.tosan.repository;

import com.tosan.model.BaseEntity;

import org.springframework.data.repository.*;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends CrudRepository<T, ID> {

}