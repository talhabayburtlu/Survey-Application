package com.layermark.survey.dao;

import com.layermark.survey.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Integer> {
    List<Topic> findByIsApprovedTrue();

    List<Topic> findByIsApprovedFalse();
}
